package com.example.Insulations;

public interface IInsulation {
    int getCoatingAmounts();
    int getDurability();
    boolean requiresTool();
    InsulationTool getRequiredTool();
}
