package com.firesoftitan.play.titanbox.telepads.runnables;

import com.firesoftitan.play.titanbox.libs.runnables.TitanSaverRunnable;
import com.firesoftitan.play.titanbox.telepads.TitanTelePads;
import com.firesoftitan.play.titanbox.telepads.managers.TelePadsManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SaveRunnable extends TitanSaverRunnable {

    public SaveRunnable() {
        super(TitanTelePads.instants);
    }

    @Override
    public void run() {
        TelePadsManager.instants.save();
    }
}
