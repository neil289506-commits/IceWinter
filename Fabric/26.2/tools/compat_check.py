#!/usr/bin/env python3
"""Check which Minecraft API references of a mod jar still resolve in a given Minecraft client jar.
usage: compat_check.py <mc-client.jar> <mod.jar> <mod-package-prefix> <out.json>"""
import sys, zipfile, struct, json, re, collections

def parse(data):
    pos = 8
    n = struct.unpack('>H', data[pos:pos+2])[0]; pos += 2
    cp = [None] * n; i = 1
    while i < n:
        tag = data[pos]; pos += 1
        if tag == 1:
            l = struct.unpack('>H', data[pos:pos+2])[0]; pos += 2
            cp[i] = ('utf8', data[pos:pos+l].decode('utf8', 'replace')); pos += l
        elif tag in (3, 4): pos += 4
        elif tag in (5, 6): pos += 8; i += 1
        elif tag == 7: cp[i] = ('class', struct.unpack('>H', data[pos:pos+2])[0]); pos += 2
        elif tag == 8: pos += 2
        elif tag in (9, 10, 11): cp[i] = ('ref', *struct.unpack('>HH', data[pos:pos+4])); pos += 4
        elif tag == 12: cp[i] = ('nat', *struct.unpack('>HH', data[pos:pos+4])); pos += 4
        elif tag == 15: pos += 3
        elif tag == 16: pos += 2
        elif tag in (17, 18): pos += 4
        elif tag in (19, 20): pos += 2
        i += 1
    def u(idx): return cp[idx][1] if cp[idx] and cp[idx][0] == 'utf8' else None
    def cname(idx): return u(cp[idx][1]) if cp[idx] and cp[idx][0] == 'class' else None
    pos += 2
    this = cname(struct.unpack('>H', data[pos:pos+2])[0]); pos += 2
    sidx = struct.unpack('>H', data[pos:pos+2])[0]; pos += 2
    sup = cname(sidx) if sidx else None
    ic = struct.unpack('>H', data[pos:pos+2])[0]; pos += 2
    ifs = []
    for _ in range(ic):
        ifs.append(cname(struct.unpack('>H', data[pos:pos+2])[0])); pos += 2
    members = {'f': set(), 'm': set()}
    for kind in ('f', 'm'):
        cnt = struct.unpack('>H', data[pos:pos+2])[0]; pos += 2
        for _ in range(cnt):
            _, ni, di, ac = struct.unpack('>HHHH', data[pos:pos+8]); pos += 8
            members[kind].add((u(ni), u(di)))
            for _ in range(ac):
                pos += 2; l = struct.unpack('>I', data[pos:pos+4])[0]; pos += 4 + l
    refs = []
    for e in cp:
        if e and e[0] == 'ref':
            nat = cp[e[2]]
            refs.append((cname(e[1]), u(nat[1]), u(nat[2]), 'f' if e[0] == 'ref' and False else None))
    return {'name': this, 'super': sup, 'ifs': ifs, 'f': members['f'], 'm': members['m'], 'cp': cp, 'u': u, 'cname': cname}

mc_zip = zipfile.ZipFile(sys.argv[1]); mod_zip = zipfile.ZipFile(sys.argv[2]); prefix = sys.argv[3]
cache = {}
def mc_class(name):
    if name in cache: return cache[name]
    try: d = parse(mc_zip.read(name + '.class'))
    except KeyError: d = None
    cache[name] = d; return d

def is_mc(n): return n and (n.startswith('net/minecraft/') or n.startswith('com/mojang/'))

def find_member(owner, name, desc, kind, seen=None):
    seen = seen or set()
    if owner in seen: return True
    seen.add(owner)
    if not is_mc(owner): return True      # JDK or library class: assume fine
    c = mc_class(owner)
    if c is None: return False
    if (name, desc) in c[kind]: return True
    if kind == 'm' and name == '<init>': return False
    for p in [c['super']] + c['ifs']:
        if p and find_member(p, name, desc, kind, seen): return True
    return False

missing_classes = set(); missing_members = collections.defaultdict(set); checked = 0
mixin_targets = {}
for info in mod_zip.infolist():
    if not info.filename.endswith('.class') or not info.filename.startswith(prefix): continue
    d = parse(mod_zip.read(info.filename)); cp = d['cp']; u = d['u']; cname = d['cname']
    # class entries and annotation descriptors
    for e in cp:
        if e and e[0] == 'class':
            n = u(e[1])
            if n and n.startswith('['): continue
            if is_mc(n) and mc_class(n) is None: missing_classes.add(n)
        elif e and e[0] == 'utf8':
            m = re.fullmatch(r'L((?:net/minecraft|com/mojang)/[A-Za-z0-9_/$]+);', e[1])
            if m:
                n = m.group(1)
                if mc_class(n) is None: missing_classes.add(n)
                if '/mixin/' in info.filename: mixin_targets.setdefault(info.filename, set()).add(n)
    for idx, e in enumerate(cp):
        if not e or e[0] != 'ref': continue
        owner = cname(e[1]); nat = cp[e[2]]
        name, desc = u(nat[1]), u(nat[2])
        if not owner or owner.startswith('[') or not is_mc(owner): continue
        # tag distinguishes field vs method by descriptor shape
        kind = 'm' if desc.startswith('(') else 'f'
        checked += 1
        if mc_class(owner) is None: missing_classes.add(owner); continue
        if not find_member(owner, name, desc, kind):
            missing_members[owner].add(f"{name}{desc}" if kind == 'm' else f"{name}:{desc}")
out = {'checked_refs': checked,
       'missing_classes': sorted(missing_classes),
       'missing_members': {k: sorted(v) for k, v in sorted(missing_members.items())},
       'mixin_targets': {k: sorted(v) for k, v in mixin_targets.items()}}
json.dump(out, open(sys.argv[4], 'w'), indent=1)
print(f"checked={checked} missing_classes={len(missing_classes)} missing_member_owners={len(missing_members)}")
