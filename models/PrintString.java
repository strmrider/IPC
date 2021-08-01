package models;

import java.nio.charset.StandardCharsets;

import interfaces.Handler;

public class PrintString implements Handler {

	@Override
	public void call(byte[] data) {
		String s = new String(data, StandardCharsets.UTF_8);
		System.out.println(s);
	}

}
