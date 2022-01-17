package com.example.MultiBlockStructure.ReactorStructure;

import com.example.Inits.TileEntityTypes;
import com.example.MultiBlockStructure.AbstractMBTileEntity;

public class ReactorTE extends AbstractMBTileEntity<Reactor> {

    public ReactorTE() {
        super(TileEntityTypes.REACTOR.get());
    }

    public ReactorTE(Reactor reactor) {
        super(TileEntityTypes.REACTOR.get());
        structure = reactor;
    }

    @Override
    public void tick() {
        super.tick();
    }
}
