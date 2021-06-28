package newpsoft.godotsvg;

import android.app.Activity;

import androidx.annotation.NonNull;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.UsedByGodot;

public class GodotSvg extends GodotPlugin {
    private Activity activity = null;

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

    @UsedByGodot
    public String haveAString() {
        return "this string from Java";
    }
}
