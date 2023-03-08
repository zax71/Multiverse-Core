package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Contract;

/**
 * A base command for Multiverse.
 */
@Contract
abstract class MultiverseCoreCommand extends MultiverseCommand {
    protected final MultiverseCore plugin;
    protected final MVWorldManager worldManager;

    protected MultiverseCoreCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
        this.plugin = plugin;
        this.worldManager = plugin.getMVWorldManager();
    }
}
