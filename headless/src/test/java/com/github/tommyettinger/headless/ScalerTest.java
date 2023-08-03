package com.github.tommyettinger.headless;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.github.tommyettinger.AtlasScaler;
import com.github.tommyettinger.Scallop;

import java.util.concurrent.atomic.AtomicInteger;

/** Launches the desktop (headless) application. */
public class ScalerTest {
	public static void main(String[] args) {
		AtomicInteger runCount = new AtomicInteger(0);
		HeadlessApplicationConfiguration config = getDefaultConfiguration();
		new HeadlessApplication(new Scallop(new String[]{"Dawnlike.atlas", "Dawnlike.png", "Eye_Tyrant.png", "font.fnt"}, runCount), config);
		new HeadlessApplication(new AtlasScaler(new String[]{"Dawnlike.atlas", "Dawnlike.png", "Eye_Tyrant.png", "font.fnt"}, runCount), config);
	}

	private static HeadlessApplicationConfiguration getDefaultConfiguration() {
		HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
		return configuration;
	}
}
