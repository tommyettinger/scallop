package com.github.tommyettinger.headless;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.github.tommyettinger.Scallop;

/** Launches the desktop (headless) application. */
public class ScalerTest {
	public static void main(String[] args) { 
		new HeadlessApplication(new Scallop(new String[]{"Eye_Tyrant.png"}), getDefaultConfiguration());
	}

	private static HeadlessApplicationConfiguration getDefaultConfiguration() {
		HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
		return configuration;
	}
}
