package net.dinomine.potioneer.item.custom;

public class Vial extends AbstractLiquidContainer  {
    public Vial(Properties pProperties) {
        super(pProperties.stacksTo(1), 1);
    }
}
