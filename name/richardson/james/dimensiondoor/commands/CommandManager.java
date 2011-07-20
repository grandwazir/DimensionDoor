
package name.richardson.james.dimensiondoor.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandManager implements CommandExecutor {

  static final List<String> commands = Arrays.asList("create", "teleport", "unload", "remove", "modify", "info", "list", "load", "spawn");

  public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
    /*
     * if (command.getName().equalsIgnoreCase("dd")) { // check of the command
     * is valid if (args.length == 0) return false; String subCommand = args[0];
     * if (!commands.contains(subCommand)) return false; if
     * (!playerHasPermission(sender, "dimensiondoor." + subCommand)) return
     * true; // execute the right command if
     * (subCommand.equalsIgnoreCase("create")) return createWorld(sender, args);
     * if (subCommand.equalsIgnoreCase("teleport")) return
     * teleportToWorld(sender, args); if (subCommand.equalsIgnoreCase("unload"))
     * return unloadWorld(sender, args); if
     * (subCommand.equalsIgnoreCase("load")) return loadWorld(sender, args); if
     * (subCommand.equalsIgnoreCase("remove")) return removeWorld(sender, args);
     * if (subCommand.equalsIgnoreCase("modify")) return modifyWorld(sender,
     * args); if (subCommand.equalsIgnoreCase("info")) return infoWorld(sender,
     * args); if (subCommand.equalsIgnoreCase("list")) return listWorlds(sender,
     * args); if (subCommand.equalsIgnoreCase("spawn")) return
     * setWorldSpawn(sender, args); }
     */
    return false;
  }

  /*
   * private boolean setWorldSpawn(CommandSender sender, String[] args) { //
   * check we are not doing this from console if
   * (getName(sender).equalsIgnoreCase("console")) {
   * sender.sendMessage(ChatColor.RED +
   * "You can not use this command from the console!"); return true; } // get
   * parameters Player player = getPlayerFromName(getName(sender)); World world
   * = player.getWorld(); // set the new location final Integer x = (int)
   * player.getLocation().getX(); final Integer y = (int)
   * player.getLocation().getY(); final Integer z = (int)
   * player.getLocation().getZ(); world.setSpawnLocation(x, y, z);
   * sender.sendMessage(ChatColor.GREEN + "New spawn location set for " +
   * world.getName()); log(Level.INFO,
   * String.format("[DimensionDoor] %s set a new spawn location on %s",
   * player.getName(), world.getName())); return true; }
   * 
   * private boolean teleportToWorld(CommandSender sender, String[] args) { //
   * check if we have enough arguments if (args.length != 2) {
   * sender.sendMessage(ChatColor.RED + "/dd teleport [world]"); return true; }
   * // check to see if destination world is loaded if
   * (!WorldRecord.isLoaded(args[1])) { sender.sendMessage(ChatColor.RED +
   * args[1] + " is not loaded!"); return true; } // check player is not console
   * if (getName(sender).equals("console")) { sender.sendMessage(ChatColor.RED +
   * "Console can not use this command"); return true; } // teleport the player
   * World destinationWorld = WorldRecord.getWorld(args[1]); Player player =
   * getPlayerFromName(getName(sender));
   * player.teleport(destinationWorld.getSpawnLocation()); return true; }
   * 
   * private boolean unloadWorld(CommandSender sender, String[] args) { // check
   * if we have enough arguments if (args.length != 2) {
   * sender.sendMessage(ChatColor.RED + "/dd unload [world]"); return true; } //
   * check to see if destination world is loaded if
   * (!WorldRecord.isLoaded(args[1])) { sender.sendMessage(ChatColor.RED +
   * args[1] + " is not loaded!"); return true; } WorldRecord world =
   * WorldRecord.find(args[1]); world.unloadWorld();
   * log.info(String.format("[DimensionDoor] %s unloaded %s", getName(sender),
   * args[1])); sender.sendMessage(ChatColor.GREEN + "World unloaded"); return
   * true; }
   * 
   * private boolean removeWorld(CommandSender sender, String[] args) { // check
   * if we have enough arguments if (args.length != 2) {
   * sender.sendMessage(ChatColor.RED + "/dd remove [world]"); return true; } //
   * check to see if destination world is loaded if
   * (!WorldRecord.isManaged(args[1])) { sender.sendMessage(ChatColor.RED +
   * args[1] + " is not managed by DimensionDoor!"); return true; } WorldRecord
   * world = WorldRecord.find(args[1]); if (WorldRecord.isLoaded(args[1]))
   * world.unloadWorld(); world.removeWorld(); log(Level.INFO,
   * String.format("[DimensionDoor] %s removed %s", getName(sender), args[1]));
   * sender.sendMessage(ChatColor.GREEN + "World unloaded"); return true; }
   * 
   * private boolean modifyWorld(CommandSender sender, String[] args) { // check
   * if we have enough arguments if (args.length != 4) {
   * sender.sendMessage(ChatColor.RED +
   * "/dd modify [world] [attribute] [value]"); return true; } // check to see
   * if destination world is loaded if (!WorldRecord.isManaged(args[1])) {
   * sender.sendMessage(ChatColor.RED + args[1] +
   * " is not managed by DimensionDoor!"); return true; } WorldRecord world =
   * WorldRecord.find(args[1]); boolean newValue =
   * Boolean.parseBoolean(args[3]); HashMap<String, Boolean> attributes =
   * world.getAttributes();
   * 
   * // check to see if the attribute is valid if
   * (!attributes.containsKey(args[2])) { sender.sendMessage(ChatColor.RED +
   * "Unknown attribute: " + args[2]); sender.sendMessage(ChatColor.YELLOW +
   * "Valid attributes: pvp, spawnAnimals, spawnMonsters, isolatedChat"); return
   * true; }
   * 
   * // save world and apply attributes attributes.put(args[2], newValue);
   * world.setAttributes(attributes); world.applyAttributes(); log(Level.INFO,
   * String.format("[DimensionDoor] %s changed %s to %s on %s", getName(sender),
   * args[2], Boolean.toString(newValue), world.getName()));
   * sender.sendMessage(ChatColor.GREEN + args[2] + " on " + args[1] +
   * " changed to " + Boolean.toString(newValue)); return true; }
   * 
   * private boolean loadWorld(CommandSender sender, String[] args) { // check
   * if we have enough arguments if (args.length != 2) {
   * sender.sendMessage(ChatColor.RED + "/dd load [world]"); return true; } //
   * check to see if destination world is loaded if
   * (WorldRecord.isLoaded(args[1])) { sender.sendMessage(ChatColor.RED +
   * args[1] + " is already loaded!"); return true; } if
   * (!WorldRecord.isManaged(args[1])) { sender.sendMessage(ChatColor.RED +
   * args[1] + " is not managed by DimensionDoor!"); return true; } WorldRecord
   * world = WorldRecord.find(args[1]); world.loadWorld(); log(Level.INFO,
   * String.format("[DimensionDoor] %s loaded %s", getName(sender), args[1]));
   * sender.sendMessage(ChatColor.GREEN + args[1] + " loaded"); return true; }
   * 
   * private boolean listWorlds(CommandSender sender, String[] args) { // get
   * all the worlds List<WorldRecord> worlds = WorldRecord.findAll();
   * StringBuilder message = new StringBuilder(); // Build the message for
   * (WorldRecord world : worlds) { if (world.isLoaded()) {
   * message.append(ChatColor.GREEN + world.getName() + ", "); } else {
   * message.append(ChatColor.RED + world.getName() + ", "); } } // get rid of
   * the trailing comma message.deleteCharAt(message.length() - 2);
   * sender.sendMessage(message.toString()); return true; }
   * 
   * 
   * private boolean infoWorld(CommandSender sender, String[] args) { // check
   * if we have enough arguments if (args.length != 2) {
   * sender.sendMessage(ChatColor.RED + "/dd info [world]"); return true; } if
   * (!WorldRecord.isManaged(args[1])) { sender.sendMessage(ChatColor.RED +
   * args[1] + " is not managed by DimensionDoor!"); return true; } WorldRecord
   * world = WorldRecord.find(args[1]); // check to see if destination world is
   * loaded if (!WorldRecord.isLoaded(args[1])) sender.sendMessage(ChatColor.RED
   * + args[1] + " is not loaded!"); else sender.sendMessage(ChatColor.GREEN +
   * args[1] + " is loaded!"); sender.sendMessage(ChatColor.YELLOW + " - seed: "
   * + Long.toString(getServer().getWorld(world.getName()).getSeed()));
   * sender.sendMessage(ChatColor.YELLOW + " - environment: " +
   * world.getEnvironment().name()); sender.sendMessage(ChatColor.YELLOW +
   * " - pvp: " + Boolean.toString(world.isPvp()));
   * sender.sendMessage(ChatColor.YELLOW + " - spawnAnimals: " +
   * Boolean.toString(world.isSpawnAnimals()));
   * sender.sendMessage(ChatColor.YELLOW + " - spawnMonsters: " +
   * Boolean.toString(world.isSpawnMonsters()));
   * sender.sendMessage(ChatColor.YELLOW + " - isolatedChat: " +
   * Boolean.toString(world.isIsolatedChat())); return true; }
   */

}
