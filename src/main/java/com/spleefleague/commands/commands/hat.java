import com.spleefleague.annotations.Endpoint;
import com.spleefleague.annotations.LiteralArg;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.commands.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class hat extends BasicCommand {

  public hat(CorePlugin plugin, String name, String usage) {
    super(plugin, new hatDispatcher(), name, usage, Rank.MODERATOR);
  }

  @Endpoint
  public void giveHat(Player p) {
    final PlayerInventory inv = p.getInventory();
    final ItemStack hand = inv.getItemInMainHand();
    final ItemStack head = inv.getHelmet();
    if(inv.getItemInMainHand().getType() != Material.AIR) {
      inv.setItemInMainHand(head);
      inv.setHelmet(hand);
      success(p, "Enjoy your new hat!");
    }else if(head != null && inv.getItemInMainHand().getType() == Material.AIR) {
      inv.setHelmet(new ItemStack(Material.AIR));
      inv.setItemInMainHand(head);
      success(p, "Your hat has been removed!");
    }else{
      error(p, "You must be holding something!");
    }
  }

  @Endpoint
  public void removeHat(Player p, @LiteralArg(value = "remove") String l) {
    final PlayerInventory inv = p.getInventory();
    final ItemStack head = inv.getHelmet();
    if(head == null || head.getType() == Material.AIR){
      error(p, "You are not wearing a hat!");
      return;
    }
    inv.setHelmet(new ItemStack(Material.AIR));
    inv.setItemInMainHand(head);
    success(p, "Your hat has been removed!");
  }
}
