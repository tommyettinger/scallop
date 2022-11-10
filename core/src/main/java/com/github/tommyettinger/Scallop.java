package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntSet;
import com.github.tommyettinger.anim8.PaletteReducer;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.github.tommyettinger.anim8.PaletteReducer.OKLAB;

public class Scallop extends ApplicationAdapter {
	public Array<String> files;
	public PaletteReducer palette;
	public Scallop(String[] filenames) {
		files = new Array<>(filenames);
		IntSet colors = new IntSet(256);
		colors.add(0);
		while (files.notEmpty() && files.first().startsWith("-")){
			String fn = files.removeIndex(0);
			if(fn.startsWith("-0x"))
				colors.add(Integer.parseUnsignedInt(fn.substring(3, 9), 16) << 8 | 255);
			else
				colors.add(Integer.parseUnsignedInt(fn.substring(1, 7), 16) << 8 | 255);
		}
		if(colors.size > 1)
			palette = new PaletteReducer(colors.iterator().toArray().items);
		else {
			palette = null;
		}
	}

	public static boolean different(int color1, int color2) {
		if (((color1 ^ color2) & 0x80) == 0x80) return true;
		final int indexA = (color1 >>> 17 & 0x7C00) | (color1 >>> 14 & 0x3E0) | (color1 >>> 11 & 0x1F),
				indexB = (color2 >>> 17 & 0x7C00) | (color2 >>> 14 & 0x3E0) | (color2 >>> 11 & 0x1F);
		final double
				L = OKLAB[0][indexA] - OKLAB[0][indexB],
				A = OKLAB[1][indexA] - OKLAB[1][indexB],
				B = OKLAB[2][indexA] - OKLAB[2][indexB];
		return (L * L + A * A + B * B) >= 0.01;
	}

		public static void scale2p(ByteBuffer dest, int A, int B, int C, int D, int E, int F, int G, int H, int I, int p0, int p1,
							   int p2, int p3) {
		dest.putInt(p0, D != 0 && ((!different(D, B) && different(B, F) && different(D, H)) || (E == 0 && B != 0)) ? D : E);
		dest.putInt(p1, B != 0 && ((!different(B, F) && different(B, D) && different(F, H)) || (E == 0 && F != 0)) ? B : E);
		dest.putInt(p2, H != 0 && ((!different(H, D) && different(D, B) && different(H, F)) || (E == 0 && D != 0)) ? H : E);
		dest.putInt(p3, F != 0 && ((!different(F, H) && different(D, H) && different(B, F)) || (E == 0 && H != 0)) ? F : E);
	}

//	public static void scale2p(ByteBuffer dest, int A, int B, int C, int D, int E, int F, int G, int H, int I, int p0, int p1,
//							   int p2, int p3) {
//		dest.putInt(p0, D != 0 && (((D&0xF0F0F0F0) == (B&0xF0F0F0F0) && (B&0xF0F0F0F0) != (F&0xF0F0F0F0) && (D&0xF0F0F0F0) != (H&0xF0F0F0F0)) || (E == 0 && B != 0)) ? D : E);
//		dest.putInt(p1, B != 0 && (((B&0xF0F0F0F0) == (F&0xF0F0F0F0) && (B&0xF0F0F0F0) != (D&0xF0F0F0F0) && (F&0xF0F0F0F0) != (H&0xF0F0F0F0)) || (E == 0 && F != 0)) ? B : E);
//		dest.putInt(p2, H != 0 && (((H&0xF0F0F0F0) == (D&0xF0F0F0F0) && (D&0xF0F0F0F0) != (B&0xF0F0F0F0) && (H&0xF0F0F0F0) != (F&0xF0F0F0F0)) || (E == 0 && D != 0)) ? H : E);
//		dest.putInt(p3, F != 0 && (((F&0xF0F0F0F0) == (H&0xF0F0F0F0) && (D&0xF0F0F0F0) != (H&0xF0F0F0F0) && (B&0xF0F0F0F0) != (F&0xF0F0F0F0)) || (E == 0 && H != 0)) ? F : E);
//	}


	public static void scale3p(ByteBuffer dest, int A, int B, int C, int D, int E, int F, int G, int H, int I, int p0, int p1,
							   int p2, int p3, int p4, int p5, int p6, int p7, int p8) {
		dest.putInt(p0, D != 0 && ((!different(D, B) && different(B, F) && different(D, H)) || (E == 0 && B != 0)) ? D : E);
		dest.putInt(p1, B != 0 && (((!different(D, B) && different(B, F) && different(D, H) && different(E, C)) || (!different(B, F) && different(B, D) && different(F, H) && different(E, A))) || (E == 0 && (D != 0 || F != 0))) ? B : E);
		dest.putInt(p2, B != 0 && ((!different(B, F) && different(B, D) && different(F, H)) || (E == 0 && F != 0)) ? B : E);
		dest.putInt(p3, D != 0 && (((!different(D, B) && different(B, F) && different(D, H) && different(E, G)) || (!different(D, H) && different(D, B) && different(H, F) && different(E, A))) || (E == 0 && (B != 0 || H != 0))) ? D : E);
		dest.putInt(p4, E);
		dest.putInt(p5, F != 0 && (((!different(B, F) && different(B, D) && different(F, H) && different(E, I)) || (!different(H, F) && different(D, H) && different(B, F) && different(E, C))) || (E == 0 && (B != 0 || H != 0))) ? F : E);
		dest.putInt(p6, H != 0 && ((!different(D, H) && different(D, B) && different(H, F)) || (E == 0 && D != 0)) ? H : E);
		dest.putInt(p7, H != 0 && (((!different(D, H) && different(D, B) && different(H, F) && different(E, I)) || (!different(H, F) && different(D, H) && different(B, F) && different(E, G))) || (E == 0 && (D != 0 || F != 0))) ? H : E);
		dest.putInt(p8, F != 0 && ((!different(H, F) && different(D, H) && different(B, F)) || (E == 0 && H != 0)) ? F : E);
	}

	public static double brightness(int rgba) {
		return OKLAB[0][PaletteReducer.shrink(rgba)] * (rgba & 0xFE);
	}
//
//	public static int brightness(int rgba) {
//		int r = rgba >>> 23 & 0x1FE;
//		int g = rgba >>> 14 & 0x3FC;
//		int b = rgba >>> 8 & 0xFF;
//		return (r + g + b) * (rgba & 0xFF);
//	}
	public static void scale2(Pixmap src, Pixmap dest) {

		final int width = src.getWidth() - 1, height = src.getHeight() - 1, dw = dest.getWidth(), dh = dest.getHeight();
		ByteBuffer pixels = dest.getPixels();

		for (int y = 0; y <= height; ++y) {
			for (int x = 0; x <= width; ++x) {
				int p0, p1, p2, p3;
				int A, B, C, D, E, F, G, H, I;

				A = x == 0 || y == 0 ? 0 : src.getPixel(x - 1, y - 1);
				B = y == 0 ? 0 : src.getPixel(x, y - 1);
				C = y == 0 || x == width ? 0 : src.getPixel(x + 1, y - 1);
				D = x == 0 ? 0 : src.getPixel(x - 1, y);
				E = src.getPixel(x, y);
				F = x == width ? 0 : src.getPixel(x + 1, y);
				G = x == 0 || y == height ? 0 : src.getPixel(x - 1, y + 1);
				H = y == height ? 0 : src.getPixel(x, y + 1);
				I = x == width || y == height ? 0 : src.getPixel(x + 1, y + 1);

				p0 = (y * dw + x << 1) << 2;
				p1 = (y * dw + x << 1 | 1) << 2;
				p2 = ((y * dw + x << 1) + dw) << 2;
				p3 = ((y * dw + x << 1 | 1) + dw) << 2;

				scale2p(pixels, A, B, C, D, E, F, G, H, I, p0, p1, p2, p3);
			}
		}

		for (int y = 1; y < dh; y++) {
			for (int x = 1; x < dw; x++) {
				int p0, p1, p2, p3, c0, c1, c2, c3;
				c0 = pixels.getInt(p0 = (y * dw + x - 1 - dw) << 2);
				c1 = pixels.getInt(p1 = (y * dw + x - dw) << 2);
				c2 = pixels.getInt(p2 = ((y * dw + x - 1)) << 2);
				c3 = pixels.getInt(p3 = ((y * dw + x)) << 2);
				if (c0 == c3 && c1 == c2 && c0 != c1)
				{
					c3 = (brightness(c0) > brightness(c1) ? c0 : c1);
					pixels.putInt(p0, c3);
					pixels.putInt(p1, c3);
					pixels.putInt(p2, c3);
					pixels.putInt(p3, c3);
				}
				else if (c0 == c3 && c0 != c1 && c0 != c2)
				{
					pixels.putInt(p1, c0);
					pixels.putInt(p2, c0);
				}
				else if (c1 == c2 && c1 != c0 && c1 != c3)
				{
					pixels.putInt(p0, c1);
					pixels.putInt(p3, c1);
				}
			}
		}
	}
	public static void scale3(Pixmap src, Pixmap dest) {

		final int width = src.getWidth() - 1, height = src.getHeight() - 1, dw = dest.getWidth(), dh = dest.getHeight();
		ByteBuffer pixels = dest.getPixels();

		for (int y = 0; y <= height; ++y) {
			for (int x = 0; x <= width; ++x) {
				int p0, p1, p2, p3, p4, p5, p6, p7, p8;
				int A, B, C, D, E, F, G, H, I;

				A = x == 0 || y == 0 ? 0 : src.getPixel(x - 1, y - 1);
				B = y == 0 ? 0 : src.getPixel(x, y - 1);
				C = y == 0 || x == width ? 0 : src.getPixel(x + 1, y - 1);
				D = x == 0 ? 0 : src.getPixel(x - 1, y);
				E = src.getPixel(x, y);
				F = x == width ? 0 : src.getPixel(x + 1, y);
				G = x == 0 || y == height ? 0 : src.getPixel(x - 1, y + 1);
				H = y == height ? 0 : src.getPixel(x, y + 1);
				I = x == width || y == height ? 0 : src.getPixel(x + 1, y + 1);

				p0 = (y * dw + x) * 3 << 2;
				p1 = (y * dw + x) * 3 + 1 << 2;
				p2 = (y * dw + x) * 3 + 2 << 2;
				p3 = (y * dw + x) * 3 + dw << 2;
				p4 = (y * dw + x) * 3 + 1 + dw << 2;
				p5 = (y * dw + x) * 3 + 2 + dw << 2;
				p6 = (y * dw + x) * 3 + dw + dw << 2;
				p7 = (y * dw + x) * 3 + 1 + dw + dw << 2;
				p8 = (y * dw + x) * 3 + 2 + dw + dw << 2;

				scale3p(pixels, A, B, C, D, E, F, G, H, I, p0, p1, p2, p3, p4, p5, p6, p7, p8);
			}
		}

		for (int y = 1; y < dh; y++) {
			for (int x = 1; x < dw; x++) {
				int p0, p1, p2, p3, c0, c1, c2, c3;
				c0 = pixels.getInt(p0 = (y * dw + x - 1 - dw) << 2);
				c1 = pixels.getInt(p1 = (y * dw + x - dw) << 2);
				c2 = pixels.getInt(p2 = ((y * dw + x - 1)) << 2);
				c3 = pixels.getInt(p3 = ((y * dw + x)) << 2);
				if (c0 == c3 && c1 == c2 && c0 != c1)
				{
					c3 = (brightness(c0) > brightness(c1) ? c0 : c1);
					pixels.putInt(p0, c3);
					pixels.putInt(p1, c3);
					pixels.putInt(p2, c3);
					pixels.putInt(p3, c3);
				}
				else if (c0 == c3 && c0 != c1 && c0 != c2)
				{
					pixels.putInt(p1, c0);
					pixels.putInt(p2, c0);
				}
				else if (c1 == c2 && c1 != c0 && c1 != c3)
				{
					pixels.putInt(p0, c1);
					pixels.putInt(p3, c1);
				}
			}
		}
	}


//	public static int lerp(int baseColor, int mixColor, float amount) {
//		if ((baseColor & 0x80) == 0)
//			return mixColor;
//		if ((mixColor & 0x80) == 0)
//			return baseColor;
//		final float i = 1f - amount;
//		final int r = (int) ((baseColor >>> 24) * i + (mixColor >>> 24) * amount), g = (int) ((baseColor >>> 16 & 0xFF) * i
//				+ (mixColor >>> 16 & 0xFF) * amount), b = (int) ((baseColor >>> 8 & 0xFF) * i + (mixColor >>> 8 & 0xFF) * amount);
//		return r << 24 | g << 16 | b << 8 | 0xFF;
//	}
//
//	public static void scale2k(ByteBuffer dest, int A, int B, int C, int D, int E, int F, int G, int H, int I, int p0, int p1,
//							   int p2, int p3) {
//		if (D == B && B != F && D != H) {
//			if (B == C && D == G) {
//				if (A != E) {
//					dest.putInt(p0, lerp(D, dest.getInt(p0), 0.75f));
//					dest.putInt(p1, lerp(D, dest.getInt(p1), 0.25f));
//					dest.putInt(p2, lerp(D, dest.getInt(p2), 0.25f));
//				}
//			} else if (B == C) {
//				dest.putInt(p0, lerp(D, dest.getInt(p0), 0.75f));
//				dest.putInt(p1, lerp(D, dest.getInt(p1), 0.25f));
//			} else if (D == G) {
//				dest.putInt(p0, lerp(D, dest.getInt(p0), 0.75f));
//				dest.putInt(p2, lerp(D, dest.getInt(p2), 0.25f));
//			} else {
//				dest.putInt(p0, lerp(D, dest.getInt(p0), 0.5f));
//			}
//		}
//	}
//
//	public static void scaleK(Pixmap src, Pixmap dest) {
//
//		final int width = src.getWidth() - 1, height = src.getHeight() - 1, dw = dest.getWidth(), dh = dest.getHeight();
//		ByteBuffer pixels = dest.getPixels();
//
//		for (int y = 0; y <= height; ++y) {
//			for (int x = 0; x <= width; ++x) {
//				int p0, p1, p2, p3;
//				int A, B, C, D, E, F, G, H, I;
//
//				A = (x & y) == 0 ? 0 : src.getPixel(x - 1, y - 1);
//				B = y == 0 ? 0 : src.getPixel(x, y - 1);
//				C = y == 0 || x == width ? 0 : src.getPixel(x + 1, y - 1);
//				D = x == 0 ? 0 : src.getPixel(x - 1, y);
//				E = src.getPixel(x, y);
//				F = x == width ? 0 : src.getPixel(x + 1, y);
//				G = x == 0 || y == height ? 0 : src.getPixel(x - 1, y + 1);
//				H = y == height ? 0 : src.getPixel(x, y + 1);
//				I = x == width || y == height ? 0 : src.getPixel(x + 1, y + 1);
//
//				p0 = (y * dw + x << 1) << 2;
//				p1 = (y * dw + x << 1 | 1) << 2;
//				p2 = ((y * dw + x << 1) + dw) << 2;
//				p3 = ((y * dw + x << 1 | 1) + dw) << 2;
//
//				scale2p(pixels, A, B, C, D, E, F, G, H, I, p0, p1, p2, p3);
//
//				scale2k(pixels, A, B, C, D, E, F, G, H, I, p0, p1, p2, p3);
//				scale2k(pixels, G, D, A, H, E, B, I, F, C, p2, p0, p3, p1);
//				scale2k(pixels, I, H, G, F, E, D, C, B, A, p3, p2, p1, p0);
//				scale2k(pixels, C, F, I, B, E, H, A, D, G, p1, p3, p0, p2);
//			}
//		}
//	}
	
	public static Pixmap load(String s) {
		boolean isAbsolute = s.matches(".*[/\\\\].*");
		FileHandle fh = isAbsolute ? Gdx.files.absolute(s) : Gdx.files.local(s);
		return new Pixmap(fh);

	}
	
	public void create() {
		PixmapIO.PNG png = new PixmapIO.PNG();
		png.setFlipY(false);
		for (String s : files) {
			boolean isAbsolute = s.matches(".*[/\\\\].*");
			FileHandle fh = isAbsolute ? Gdx.files.absolute(s) : Gdx.files.local(s);
			Pixmap source = new Pixmap(fh);
			Pixmap dest2 = new Pixmap(source.getWidth() * 2, source.getHeight() * 2, Pixmap.Format.RGBA8888);
			Pixmap dest3 = new Pixmap(source.getWidth() * 3, source.getHeight() * 3, Pixmap.Format.RGBA8888);
			Pixmap dest4 = new Pixmap(source.getWidth() * 4, source.getHeight() * 4, Pixmap.Format.RGBA8888);
			Pixmap dest6 = new Pixmap(source.getWidth() * 6, source.getHeight() * 6, Pixmap.Format.RGBA8888);
			Pixmap dest8 = new Pixmap(source.getWidth() * 8, source.getHeight() * 8, Pixmap.Format.RGBA8888);
			scale2(source, dest2);
			scale3(source, dest3);
			scale2(dest2, dest4);
			scale3(dest2, dest6);
			scale2(dest4, dest8);
			if(palette != null){
				palette.setDitherStrength(0.5f);
				palette.reduceWoven(source);
				palette.reduceWoven(dest2);
				palette.reduceWoven(dest3);
				palette.reduceWoven(dest4);
				palette.reduceWoven(dest6);
				palette.reduceWoven(dest8);
			}
			try {
				if(isAbsolute) {
					png.write(Gdx.files.absolute(fh.pathWithoutExtension() + "-x1.png"), source);
					png.write(Gdx.files.absolute(fh.pathWithoutExtension() + "-x2.png"), dest2);
					png.write(Gdx.files.absolute(fh.pathWithoutExtension() + "-x3.png"), dest3);
					png.write(Gdx.files.absolute(fh.pathWithoutExtension() + "-x4.png"), dest4);
					png.write(Gdx.files.absolute(fh.pathWithoutExtension() + "-x6.png"), dest6);
					png.write(Gdx.files.absolute(fh.pathWithoutExtension() + "-x8.png"), dest8);
				}
				else {
					png.write(Gdx.files.local(fh.pathWithoutExtension() + "-x1.png"), source);
					png.write(Gdx.files.local(fh.pathWithoutExtension() + "-x2.png"), dest2);
					png.write(Gdx.files.local(fh.pathWithoutExtension() + "-x3.png"), dest3);
					png.write(Gdx.files.local(fh.pathWithoutExtension() + "-x4.png"), dest4);
					png.write(Gdx.files.local(fh.pathWithoutExtension() + "-x6.png"), dest6);
					png.write(Gdx.files.local(fh.pathWithoutExtension() + "-x8.png"), dest8);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				source.dispose();
				dest2.dispose();
				dest3.dispose();
				dest4.dispose();
				dest6.dispose();
				dest8.dispose();
			}
		}
		Gdx.app.exit();
	}
}
