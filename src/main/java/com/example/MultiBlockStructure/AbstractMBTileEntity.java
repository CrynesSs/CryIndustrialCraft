package com.example.MultiBlockStructure;

import com.example.Util.MultiBlocks.StructureSave;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.server.ServerWorld;

public class AbstractMBTileEntity<T extends AbstractMBStructure> extends TileEntity implements ITickableTileEntity {
    protected AbstractMBStructure structure;

    public AbstractMBTileEntity(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }

    public int cooldown = 0;

    @Override
    public void onLoad() {
        if (this.level instanceof ServerWorld) {
            StructureSave save = ((ServerWorld) this.level).getDataStorage().get(StructureSave::new, StructureSave.NAME);
            if (save == null) return;
            save.STRUCTURES.parallelStream().filter(structure -> structure.getCorner().equals(this.worldPosition)).findFirst().ifPresent(structure -> this.structure = structure);
        }
        super.onLoad();
    }

    public AbstractMBStructure getStructure() {
        return structure;
    }

    @Override
    public void tick() {
        if (cooldown == 20) {
            System.out.println("I am a piece of Shit at Position : " + this.worldPosition);
            cooldown = 0;
        }
        cooldown++;
    }
}
