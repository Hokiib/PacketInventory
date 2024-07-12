package fr.hokib.packetinventory;

import com.comphenix.packetwrapper.wrappers.play.clientbound.WrapperPlayServerSetSlot;
import com.comphenix.packetwrapper.wrappers.play.clientbound.WrapperPlayServerWindowItems;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class PacketInventory extends JavaPlugin implements Listener {

    private ProtocolManager manager;
    private boolean enabled = false;

    @Override
    public void onEnable() {
        this.manager = ProtocolLibrary.getProtocolManager();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onChat(PlayerChatEvent e){
        if(e.getMessage().equalsIgnoreCase("test")){
            this.toggle(e.getPlayer(), this.enabled = !this.enabled);
        }
    }

    public void toggle(final Player player, final boolean state){
        if(!state){
            player.updateInventory();
            Bukkit.broadcastMessage("Disable");
        } else {
            Bukkit.broadcastMessage("Enable");

            final ItemStack[] contents = player.getInventory().getContents();
            final List<Integer> slots = new ArrayList<>();
            for (int i = 0; i < contents.length; i++) {
                final ItemStack item = contents[i];
                if(item == null) continue;
                if(item.getType().isAir()) continue;

                if(i <= 8) slots.add(36 + i);
                else slots.add(i);
            }

            for (final int slot : slots) {
                this.manager.sendServerPacket(player, getPacket(slot, new ItemStack(Material.AIR)));
            }

            int slot = 36;
            for (ItemStack item : ITEMS) {
                this.manager.sendServerPacket(player, getPacket(slot++, item));
            }
        }
    }

    private static final List<ItemStack> ITEMS = List.of(new ItemStack(Material.DIAMOND), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.IRON_INGOT));

    public PacketContainer getPacket(final int slot, final ItemStack itemStack){
        final WrapperPlayServerSetSlot p = new WrapperPlayServerSetSlot();
        p.setSlot(slot);
        p.setContainerId(0);
        p.setItemStack(itemStack);

        return p.getHandle();
    }
}
