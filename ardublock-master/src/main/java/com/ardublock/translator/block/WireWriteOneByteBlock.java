package com.ardublock.translator.block;

import com.ardublock.Context;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class WireWriteOneByteBlock extends TranslatorBlock
{
	public WireWriteOneByteBlock(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		WireReadBlock.setupWireEnvironment(translator);
	
		String ret = "";
		Context context = translator.getContext();
		if (context.getArduinoVersionString().equals(Context.ARDUINO_VERSION_UNKNOWN))
		{
			//ret += "//Unable to dectect your Arduino version, using 1.0 in default\n";
			System.err.println("//Unable to dectect your Arduino version, using 1.0 in default");
		}
		
		ret += "__ardublockI2cWriteDataOne( ";
		TranslatorBlock tb = getRequiredTranslatorBlockAtSocket(0);
		ret = ret + tb.toCode();
		ret = ret + " , ";
		tb = getRequiredTranslatorBlockAtSocket(1);
		ret = ret + tb.toCode();
		ret = ret + " );\n";
		return ret;
	}

}
