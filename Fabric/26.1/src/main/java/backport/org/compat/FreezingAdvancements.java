package backport.org.compat;

import com.google.gson.JsonElement;
import net.minecraft.advancements.Advancement;

public final class FreezingAdvancements {
	private FreezingAdvancements() {}

	public static Advancement extend(String id, Advancement original) {
		return original;
	}

	public static JsonElement extendJson(String id, JsonElement original) {
		return original;
	}
}
