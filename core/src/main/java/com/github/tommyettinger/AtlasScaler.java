package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AtlasScaler extends ApplicationAdapter {
    public Array<String> atlases, fonts;

    public AtlasScaler(String[] filenames) {
        atlases = new Array<>(filenames.length);
        fonts = new Array<>(filenames.length);
        for (int i = 0; i < filenames.length; i++) {
            if(filenames[i].endsWith(".atlas"))
                atlases.add(filenames[i]);
            else if(filenames[i].endsWith(".fnt"))
                fonts.add(filenames[i]);
        }
    }

    public FileHandle load(String name) {
        FileHandle fh = Gdx.files.local(name);
        if(fh.exists()) return fh;
        fh = Gdx.files.absolute(name);
        if(fh.exists()) return fh;
        return null;
    }

    @Override
    public void create() {
/* ;; original Clojure code, meant for a REPL
(doseq [n [2 3 4]]
 (spit (str "Dawnlike" n ".atlas")
       (-> text
           (clojure.string/replace "Dawnlike.png"
                                   (str "Dawnlike" n ".png"))
           (clojure.string/replace #"(\d+),(?: ?)(\d+)"
                                   (fn [[_ l r]]
                                       (str
                                         (* (Integer/parseInt l) n)
                                         ","
                                         (* (Integer/parseInt r) n)))))))
 */
        final Pattern pairs = Pattern.compile("(\\d+), ?(\\d+)");
        for(String name : atlases) {
            FileHandle original = load(name);
            if(original == null) continue;
            String text = original.readString("UTF8");
            TextureAtlas.TextureAtlasData atlas = new TextureAtlas.TextureAtlasData(original, original.parent(), false);
            Array<TextureAtlas.TextureAtlasData.Page> pages = atlas.getPages();
            for (int n : new int[]{2, 3, 4, 6, 8}) {
                String outputName = (original.pathWithoutExtension() + "-x" + n + ".atlas");
                FileHandle output;
                if(original.type() == Files.FileType.Local)
                    output = Gdx.files.local(outputName);
                else
                    output = Gdx.files.absolute(outputName);
                String working = text;
                for(TextureAtlas.TextureAtlasData.Page p : pages){
                    FileHandle th = p.textureFile;
                    working = working.replaceAll(Pattern.quote(th.name()), th.nameWithoutExtension() + "-x" + n + "." + th.extension());
                }
                Matcher matcher = pairs.matcher(working);
                StringBuffer buf = new StringBuffer(working.length());
                while (matcher.find()){
                    int left = Integer.parseInt(matcher.group(1)) * n, right = Integer.parseInt(matcher.group(2)) * n;
                    matcher.appendReplacement(buf, left + "," + right);
                }
                matcher.appendTail(buf);
                output.writeString(buf.toString(), false, "UTF8");
            }
        }
/*
(doseq [n [2 3 4]]
  (spit (str "font" n ".fnt")
        (clojure.string/replace fnt
#" x=([\-0-9]+) y=([\-0-9]+) width=([\-0-9]+) height=([\-0-9]+) xoffset=([\-0-9]+) yoffset=([\-0-9]+) xadvance=([\-0-9]+)"
 (fn[[_ x y w h xo yo xa]] (str
    " x=" (* n (read-string x))
    " y="(* n (read-string y))
    " width="(* n (read-string w))
    " height="(* n (read-string h))
    " xoffset="(* n (read-string xo))
    " yoffset="(* n (read-string yo))
    " xadvance="(* n (read-string xa)) )))))

*/
        Pattern fontLine = Pattern.compile(" x=([\\-0-9]+) y=([\\-0-9]+) width=([\\-0-9]+) height=([\\-0-9]+) xoffset=([\\-0-9]+) yoffset=([\\-0-9]+) xadvance=([\\-0-9]+)", Pattern.CASE_INSENSITIVE);
        for(String name : fonts) {
            FileHandle original = load(name);
            if (original == null) continue;
            String text = original.readString("UTF8");
            for (int n : new int[]{2, 3, 4, 6, 8}) {
                String outputName = (original.pathWithoutExtension() + "-x" + n + ".fnt");
                FileHandle output;
                if (original.type() == Files.FileType.Local)
                    output = Gdx.files.local(outputName);
                else
                    output = Gdx.files.absolute(outputName);
                Matcher matcher = fontLine.matcher(text);
                StringBuffer buf = new StringBuffer(text.length());
                while (matcher.find()){
                    int
                            x= Integer.parseInt(matcher.group(1)) * n,
                            y= Integer.parseInt(matcher.group(2)) * n,
                            width= Integer.parseInt(matcher.group(3)) * n,
                            height= Integer.parseInt(matcher.group(4)) * n,
                            xoffset= Integer.parseInt(matcher.group(5)) * n,
                            yoffset= Integer.parseInt(matcher.group(6)) * n,
                            xadvance= Integer.parseInt(matcher.group(7)) * n;
                    matcher.appendReplacement(buf,
                            " x=" + x + " y=" + y +
                                    " width=" + width + " height=" + height +
                                    " xoffset=" + xoffset + " yoffset=" + yoffset +
                                    " xadvance=" + xadvance);
                }
                matcher.appendTail(buf);
                output.writeString(buf.toString(), false, "UTF8");

            }
        }
//        Gdx.app.postRunnable(() -> {
//            System.exit(0);
//        });
    }
}
