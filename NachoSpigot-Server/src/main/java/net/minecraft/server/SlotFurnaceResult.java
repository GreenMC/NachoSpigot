package net.minecraft.server;

// CraftBukkit start
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceExtractEvent;
// CraftBukkit end

public class SlotFurnaceResult extends Slot {

    private final EntityHuman a;
    private int b;

    public SlotFurnaceResult(EntityHuman entityhuman, IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
        this.a = entityhuman;
    }

    public boolean isAllowed(ItemStack itemstack) {
        return false;
    }

    public ItemStack a(int i) {
        if (this.hasItem()) {
            this.b += Math.min(i, this.getItem().count);
        }

        return super.a(i);
    }

    public void a(EntityHuman entityhuman, ItemStack itemstack) {
        this.c(itemstack);
        super.a(entityhuman, itemstack);
    }

    protected void a(ItemStack itemstack, int i) {
        this.b += i;
        this.c(itemstack);
    }

    protected void c(ItemStack itemstack) {
        itemstack.a(this.a.world, this.a, this.b);
        if (!this.a.world.isClientSide) {
            int i = this.b;
            float f = RecipesFurnace.getInstance().b(itemstack);
            int j;

            if (f == 0.0F) {
                i = 0;
            } else if (f < 1.0F) {
                j = MathHelper.d((float) i * f);
                if (j < MathHelper.f((float) i * f) && Math.random() < (double) ((float) i * f - (float) j)) {
                    ++j;
                }

                i = j;
            }

            // CraftBukkit start - fire FurnaceExtractEvent
            Player player = (Player) a.getBukkitEntity();
            TileEntityFurnace furnace = ((TileEntityFurnace) this.inventory);
            org.bukkit.block.Block block = a.world.getWorld().getBlockAt(furnace.position.getX(), furnace.position.getY(), furnace.position.getZ());

            if (b != 0) {
                FurnaceExtractEvent event = new FurnaceExtractEvent(player, block, org.bukkit.craftbukkit.util.CraftMagicNumbers.getMaterial(itemstack.getItem()), b, i);
                a.world.getServer().getPluginManager().callEvent(event);
                i = event.getExpToDrop();
            }
            // CraftBukkit end

            while (i > 0) {
                j = EntityExperienceOrb.getOrbValue(i);
                i -= j;
                this.a.world.addEntity(new EntityExperienceOrb(this.a.world, this.a.locX, this.a.locY + 0.5D, this.a.locZ + 0.5D, j));
            }
        }

        this.b = 0;
        if (itemstack.getItem() == Items.IRON_INGOT) {
            this.a.b(AchievementList.k);
        }

        if (itemstack.getItem() == Items.COOKED_FISH) {
            this.a.b(AchievementList.p);
        }

    }
}
