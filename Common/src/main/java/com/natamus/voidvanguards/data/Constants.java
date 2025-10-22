package com.natamus.voidvanguards.data;

import com.natamus.voidvanguards.util.Reference;

public class Constants {
	public static final String saveDataTagPrefix = Reference.NAME.replace(" ", "") + ".savedata";
	public static final String saveDataTagMainDelimiter = "--";
	public static final String saveDataTagSubDelimiter = "..";

	public static final String forcedPositionTagPrefix = Reference.MOD_ID + ".forceposition";
	public static final String sendMessageTagPrefix = Reference.MOD_ID + ".sendmessage";
	public static final String triggeredEntityInsideTag = Reference.MOD_ID + ".triggeredEntityInside";
}
