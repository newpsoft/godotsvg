package newpsoft.godotsvg;

/* GodotSvg - A Godot Android plugin to read SVG files.
  Copyright (C) 2021 Jonathan Pelletier, New Paradigm Software

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import androidx.annotation.NonNull;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import org.godotengine.godot.Dictionary;
import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.UsedByGodot;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Read SVG files and data, and convert to PNG as raw bytes.
 */
public class GodotSvg extends GodotPlugin {
    private Activity activity;

    public GodotSvg(Godot godot) {
        super(godot);
        activity = getActivity();
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public String getPluginName() {
        return "GodotSvg";
    }

    /**
     * Read an SVG file from local filesystem, and convert it to PNG as a raw byte array.
     * <p>
     * If either width or height are 0 we first try to determine the image size from the SVG
     * document declared size.  If the document also has no size declared we fallback to a small
     * square.
     *
     * @param fileName Local file path.  A file URI will not work.
     * @param width    Absolute pixel width we want to draw into.  A value of 0 will default to what
     *                 is declared in the SVG document.
     * @param height   Absolute pixel height we want to draw into.  A value of 0 will default to what
     *                 is declared in the SVG document.
     * @return Map possible value types: {"success": Boolean, "value": byte[], "error": String}
     */
    @UsedByGodot
    public Dictionary fileToPng(String fileName, int width, int height) {
        Dictionary ret = new Dictionary();
        FileInputStream is = null;
        try {
            is = new FileInputStream(fileName);
            ret.put("value", streamToPng(is, width, height));
            ret.put("success", true);
        } catch (FileNotFoundException | SVGParseException e) {
            e.printStackTrace();
            ret.put("success", false);
            ret.put("error", e.toString());
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Read an SVG raw resource, and convert it to PNG as a raw byte array.
     * <p>
     * If either width or height are 0 we first try to determine the image size from the SVG
     * document declared size.  If the document also has no size declared we fallback to a small
     * square.
     *
     * @param resourceId Resource ID of the resource to read from, e.g. R.raw.filename_svg
     * @param width      Absolute pixel width we want to draw into.  A value of 0 will default to what
     *                   is declared in the SVG document.
     * @param height     Absolute pixel height we want to draw into.  A value of 0 will default to what
     *                   is declared in the SVG document.
     * @return Map possible value types: {"success": Boolean, "value": byte[], "error": String}
     */
    @UsedByGodot
    public Dictionary resourceToPng(int resourceId, int width, int height) {
        Dictionary ret = new Dictionary();
        InputStream is = null;
        try {
            is = activity.getResources().openRawResource(resourceId);
            ret.put("value", streamToPng(is, width, height));
            ret.put("success", true);
        } catch (SVGParseException e) {
            e.printStackTrace();
            ret.put("success", false);
            ret.put("error", e.toString());
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Read an SVG from packaged assets, and convert it to PNG as a raw byte array.
     * <p>
     * If either width or height are 0 we first try to determine the image size from the SVG
     * document declared size.  If the document also has no size declared we fallback to a small
     * square.
     *
     * @param assetPath Asset path to the SVG file, e.g. "images/filename.svg"
     * @param width     Absolute pixel width we want to draw into.  A value of 0 will default to what
     *                  is declared in the SVG document.
     * @param height    Absolute pixel height we want to draw into.  A value of 0 will default to what
     *                  is declared in the SVG document.
     * @return Map possible value types: {"success": Boolean, "value": byte[], "error": String}
     */
    @UsedByGodot
    public Dictionary assetToPng(String assetPath, int width, int height) {
        Dictionary ret = new Dictionary();
        InputStream is = null;
        try {
            is = activity.getAssets().open(assetPath);
            ret.put("value", streamToPng(is, width, height));
            ret.put("success", true);
        } catch (IOException | SVGParseException e) {
            e.printStackTrace();
            ret.put("success", false);
            ret.put("error", e.toString());
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Read the SVG stream and convert to PNG.  Throws null exception if InputStream is null.
     */
    private byte[] streamToPng(InputStream is, int width, int height) throws SVGParseException {
        SVG svg = SVG.getFromInputStream(is);
        /* Sanity check or find default or backup boundaries.  Both or neither. */
        if (width == 0 || height == 0) {
            /* Without an explicit size we first default to the document declared size. */
            if (svg.getDocumentWidth() > 0 && svg.getDocumentHeight() > 0) {
                width = (int) svg.getDocumentWidth();
                height = (int) svg.getDocumentHeight();
            } else {
                /* Backup default square canvas */
                width = height = 512;
            }
        }

        Bitmap newBM = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(newBM);
        // TODO: Confirm reloading image to TextureView does not require a white background.
        // Clear background to white
//            canvas.drawRGB(255, 255, 255);

        // Render our document onto our canvas
        svg.renderToCanvas(new Canvas(newBM));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        newBM.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }
}
