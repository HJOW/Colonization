package org.duckdns.hjow.colonization.pack;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.NormalColony;
import org.duckdns.hjow.colonization.elements.facilities.Arcade;
import org.duckdns.hjow.colonization.elements.facilities.ArchitectOffice;
import org.duckdns.hjow.colonization.elements.facilities.CapsuleBusStation;
import org.duckdns.hjow.colonization.elements.facilities.CargoRailSystem;
import org.duckdns.hjow.colonization.elements.facilities.MagneticLevitationMetroStation;
import org.duckdns.hjow.colonization.elements.facilities.MiniCenter;
import org.duckdns.hjow.colonization.elements.facilities.PowerStation;
import org.duckdns.hjow.colonization.elements.facilities.ResidenceModule;
import org.duckdns.hjow.colonization.elements.facilities.Restaurant;
import org.duckdns.hjow.colonization.elements.facilities.SmallAntenna;
import org.duckdns.hjow.colonization.elements.facilities.SmallApartment;
import org.duckdns.hjow.colonization.elements.facilities.SmallFactory;
import org.duckdns.hjow.colonization.elements.facilities.SmallResearchCenter;
import org.duckdns.hjow.colonization.elements.facilities.SolarStation;
import org.duckdns.hjow.colonization.elements.facilities.TownHouse;
import org.duckdns.hjow.colonization.elements.facilities.Turret;
import org.duckdns.hjow.colonization.elements.products.food.NutritionBlock;
import org.duckdns.hjow.colonization.elements.research.BasicScience;
import org.duckdns.hjow.colonization.elements.research.biology.BasicBiology;
import org.duckdns.hjow.colonization.elements.research.biology.BasicMedicalScience;
import org.duckdns.hjow.colonization.elements.research.chemical.Chemical;
import org.duckdns.hjow.colonization.elements.research.chemical.NewMetals;
import org.duckdns.hjow.colonization.elements.research.energy.EnergyTech;
import org.duckdns.hjow.colonization.elements.research.energy.LightTech;
import org.duckdns.hjow.colonization.elements.research.engineering.BasicBuildingTech;
import org.duckdns.hjow.colonization.elements.research.engineering.BasicEngineering;
import org.duckdns.hjow.colonization.elements.research.humanities.BasicHumanities;
import org.duckdns.hjow.colonization.elements.research.military.MilitaryTech;
import org.duckdns.hjow.colonization.elements.states.ImmuneInfluenza;
import org.duckdns.hjow.colonization.elements.states.Influenza;
import org.duckdns.hjow.colonization.elements.states.SuperAngry;

/** 기본 제공 Pack */
public final class BundledPack extends DefaultPack {
    private static final long serialVersionUID = -1884375631795840563L;
    
    @Override
    protected String getDefaultName() { return ColonyManager.t("표준") + " Pack"; }
    
    @Override
    protected String getDefaultDesc() { return "Colonization " + ColonyManager.t("표준") + " Pack"; }
    
    @Override
    protected String getDefaultAuthor() { return "HJOW"; }
    
    @Override
    protected String getDefaultEmail()  { return "hujinone22@naver.com"; }
    
    @Override
    protected void init() {
        colonyClasses.add(NormalColony.class);
        
        facilityClasses.add(ResidenceModule.class);
        facilityClasses.add(PowerStation.class);
        facilityClasses.add(Restaurant.class);
        facilityClasses.add(Arcade.class);
        facilityClasses.add(SmallFactory.class);
        facilityClasses.add(SmallResearchCenter.class);
        facilityClasses.add(ArchitectOffice.class);
        facilityClasses.add(CapsuleBusStation.class);
        facilityClasses.add(MagneticLevitationMetroStation.class);
        facilityClasses.add(Turret.class);
        facilityClasses.add(SmallAntenna.class);
        facilityClasses.add(MiniCenter.class);
        facilityClasses.add(TownHouse.class);
        facilityClasses.add(SmallApartment.class);
        facilityClasses.add(SolarStation.class);
        facilityClasses.add(CargoRailSystem.class);
        
        researchClasses.add(BasicScience.class);
        researchClasses.add(BasicHumanities.class);
        researchClasses.add(MilitaryTech.class);
        researchClasses.add(BasicBuildingTech.class);
        researchClasses.add(BasicBiology.class);
        researchClasses.add(BasicMedicalScience.class);
        researchClasses.add(BasicEngineering.class);
        researchClasses.add(EnergyTech.class);
        researchClasses.add(NewMetals.class);
        researchClasses.add(LightTech.class);
        researchClasses.add(Chemical.class);
        
        stateClasses.add(Influenza.class);
        stateClasses.add(ImmuneInfluenza.class);
        stateClasses.add(SuperAngry.class);
        
        productClasses.add(NutritionBlock.class);
        
    }
}
