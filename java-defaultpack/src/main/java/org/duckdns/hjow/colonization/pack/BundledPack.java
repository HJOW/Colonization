package org.duckdns.hjow.colonization.pack;

import org.duckdns.hjow.commons.util.classwrapper.SimpleClassWrapper;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.NormalColony;
import org.duckdns.hjow.colonization.elements.facilities.Academy;
import org.duckdns.hjow.colonization.elements.facilities.Arcade;
import org.duckdns.hjow.colonization.elements.facilities.ArchitectOffice;
import org.duckdns.hjow.colonization.elements.facilities.BigFactory;
import org.duckdns.hjow.colonization.elements.facilities.CapsuleBusStation;
import org.duckdns.hjow.colonization.elements.facilities.CargoRailSystem;
import org.duckdns.hjow.colonization.elements.facilities.FissionReactorStation;
import org.duckdns.hjow.colonization.elements.facilities.LaserCannon;
import org.duckdns.hjow.colonization.elements.facilities.MagneticLevitationMetroStation;
import org.duckdns.hjow.colonization.elements.facilities.MiniCenter;
import org.duckdns.hjow.colonization.elements.facilities.PowerStation;
import org.duckdns.hjow.colonization.elements.facilities.ResidenceModule;
import org.duckdns.hjow.colonization.elements.facilities.Restaurant;
import org.duckdns.hjow.colonization.elements.facilities.SmallAntenna;
import org.duckdns.hjow.colonization.elements.facilities.SmallApartment;
import org.duckdns.hjow.colonization.elements.facilities.SmallFactory;
import org.duckdns.hjow.colonization.elements.facilities.SmallPort;
import org.duckdns.hjow.colonization.elements.facilities.SmallResearchCenter;
import org.duckdns.hjow.colonization.elements.facilities.SolarStation;
import org.duckdns.hjow.colonization.elements.facilities.TownHouse;
import org.duckdns.hjow.colonization.elements.facilities.Turret;
import org.duckdns.hjow.colonization.elements.policies.FertilityPromotionPolicy;
import org.duckdns.hjow.colonization.elements.policies.PowerEfficiencyProtocol;
import org.duckdns.hjow.colonization.elements.products.food.NutritionBlock;
import org.duckdns.hjow.colonization.elements.products.metal.Iron1;
import org.duckdns.hjow.colonization.elements.products.metal.Iron2;
import org.duckdns.hjow.colonization.elements.research.BasicScience;
import org.duckdns.hjow.colonization.elements.research.biology.BasicBiology;
import org.duckdns.hjow.colonization.elements.research.biology.BasicMedicalScience;
import org.duckdns.hjow.colonization.elements.research.biology.GeneTech;
import org.duckdns.hjow.colonization.elements.research.chemical.Chemical;
import org.duckdns.hjow.colonization.elements.research.chemical.NewMetals;
import org.duckdns.hjow.colonization.elements.research.chemical.Plasteel;
import org.duckdns.hjow.colonization.elements.research.energy.ElectroMagneticTech;
import org.duckdns.hjow.colonization.elements.research.energy.EnergyTech;
import org.duckdns.hjow.colonization.elements.research.energy.FissionReactor;
import org.duckdns.hjow.colonization.elements.research.energy.Laser;
import org.duckdns.hjow.colonization.elements.research.energy.LightTech;
import org.duckdns.hjow.colonization.elements.research.energy.Nuclear;
import org.duckdns.hjow.colonization.elements.research.energy.NuclearFusion;
import org.duckdns.hjow.colonization.elements.research.energy.Plasma;
import org.duckdns.hjow.colonization.elements.research.engineering.BasicBuildingTech;
import org.duckdns.hjow.colonization.elements.research.engineering.BasicEngineering;
import org.duckdns.hjow.colonization.elements.research.engineering.ComputerTech;
import org.duckdns.hjow.colonization.elements.research.engineering.ConstructionDrones;
import org.duckdns.hjow.colonization.elements.research.engineering.Printing3DStructure;
import org.duckdns.hjow.colonization.elements.research.humanities.BasicHumanities;
import org.duckdns.hjow.colonization.elements.research.military.MilitaryTech;
import org.duckdns.hjow.colonization.elements.research.physics.Mathematics;
import org.duckdns.hjow.colonization.elements.research.physics.Physics;
import org.duckdns.hjow.colonization.elements.ship.Willamette;
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
        colonyClasses.add(new SimpleClassWrapper(NormalColony.class));
        
        facilityClasses.add(new SimpleClassWrapper(ResidenceModule.class));
        facilityClasses.add(new SimpleClassWrapper(TownHouse.class));
        facilityClasses.add(new SimpleClassWrapper(SmallApartment.class));
        facilityClasses.add(new SimpleClassWrapper(PowerStation.class));
        facilityClasses.add(new SimpleClassWrapper(Restaurant.class));
        facilityClasses.add(new SimpleClassWrapper(Arcade.class));
        facilityClasses.add(new SimpleClassWrapper(SmallFactory.class));
        facilityClasses.add(new SimpleClassWrapper(BigFactory.class));
        facilityClasses.add(new SimpleClassWrapper(SmallResearchCenter.class));
        facilityClasses.add(new SimpleClassWrapper(ArchitectOffice.class));
        facilityClasses.add(new SimpleClassWrapper(CapsuleBusStation.class));
        facilityClasses.add(new SimpleClassWrapper(MagneticLevitationMetroStation.class));
        facilityClasses.add(new SimpleClassWrapper(Turret.class));
        facilityClasses.add(new SimpleClassWrapper(LaserCannon.class));
        facilityClasses.add(new SimpleClassWrapper(SmallAntenna.class));
        facilityClasses.add(new SimpleClassWrapper(MiniCenter.class));
        facilityClasses.add(new SimpleClassWrapper(SolarStation.class));
        facilityClasses.add(new SimpleClassWrapper(FissionReactorStation.class));
        facilityClasses.add(new SimpleClassWrapper(CargoRailSystem.class));
        facilityClasses.add(new SimpleClassWrapper(Academy.class));
        facilityClasses.add(new SimpleClassWrapper(SmallPort.class));
        
        researchClasses.add(new SimpleClassWrapper(Mathematics.class));
        researchClasses.add(new SimpleClassWrapper(BasicScience.class));
        researchClasses.add(new SimpleClassWrapper(ElectroMagneticTech.class));
        researchClasses.add(new SimpleClassWrapper(BasicHumanities.class));
        researchClasses.add(new SimpleClassWrapper(MilitaryTech.class));
        researchClasses.add(new SimpleClassWrapper(BasicBuildingTech.class));
        researchClasses.add(new SimpleClassWrapper(BasicBiology.class));
        researchClasses.add(new SimpleClassWrapper(BasicMedicalScience.class));
        researchClasses.add(new SimpleClassWrapper(BasicEngineering.class));
        researchClasses.add(new SimpleClassWrapper(ComputerTech.class));
        researchClasses.add(new SimpleClassWrapper(EnergyTech.class));
        researchClasses.add(new SimpleClassWrapper(NewMetals.class));
        researchClasses.add(new SimpleClassWrapper(LightTech.class));
        researchClasses.add(new SimpleClassWrapper(Laser.class));
        researchClasses.add(new SimpleClassWrapper(Plasma.class));
        researchClasses.add(new SimpleClassWrapper(Chemical.class));
        researchClasses.add(new SimpleClassWrapper(Physics.class));
        researchClasses.add(new SimpleClassWrapper(GeneTech.class));
        researchClasses.add(new SimpleClassWrapper(Nuclear.class));
        researchClasses.add(new SimpleClassWrapper(FissionReactor.class));
        researchClasses.add(new SimpleClassWrapper(Plasteel.class));
        researchClasses.add(new SimpleClassWrapper(Printing3DStructure.class));
        researchClasses.add(new SimpleClassWrapper(ConstructionDrones.class));
        researchClasses.add(new SimpleClassWrapper(NuclearFusion.class));
        
        stateClasses.add(new SimpleClassWrapper(Influenza.class));
        stateClasses.add(new SimpleClassWrapper(ImmuneInfluenza.class));
        stateClasses.add(new SimpleClassWrapper(SuperAngry.class));
        
        productClasses.add(new SimpleClassWrapper(NutritionBlock.class));
        productClasses.add(new SimpleClassWrapper(Iron1.class));
        productClasses.add(new SimpleClassWrapper(Iron2.class));
        
        policyClasses.add(new SimpleClassWrapper(PowerEfficiencyProtocol.class));
        policyClasses.add(new SimpleClassWrapper(FertilityPromotionPolicy.class));
        
        shipClasses.add(new SimpleClassWrapper(Willamette.class));
    }
}
