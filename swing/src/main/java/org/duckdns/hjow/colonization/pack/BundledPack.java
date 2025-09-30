package org.duckdns.hjow.colonization.pack;

import org.duckdns.hjow.colonization.elements.NormalColony;
import org.duckdns.hjow.colonization.elements.facilities.Arcade;
import org.duckdns.hjow.colonization.elements.facilities.ArchitectOffice;
import org.duckdns.hjow.colonization.elements.facilities.BusStation;
import org.duckdns.hjow.colonization.elements.facilities.Factory;
import org.duckdns.hjow.colonization.elements.facilities.PowerStation;
import org.duckdns.hjow.colonization.elements.facilities.ResearchCenter;
import org.duckdns.hjow.colonization.elements.facilities.ResidenceModule;
import org.duckdns.hjow.colonization.elements.facilities.Restaurant;
import org.duckdns.hjow.colonization.elements.facilities.TownHouse;
import org.duckdns.hjow.colonization.elements.facilities.Turret;
import org.duckdns.hjow.colonization.elements.research.BasicBiology;
import org.duckdns.hjow.colonization.elements.research.BasicBuildingTech;
import org.duckdns.hjow.colonization.elements.research.BasicEngineering;
import org.duckdns.hjow.colonization.elements.research.BasicHumanities;
import org.duckdns.hjow.colonization.elements.research.BasicMedicalScience;
import org.duckdns.hjow.colonization.elements.research.BasicScience;
import org.duckdns.hjow.colonization.elements.research.MilitaryTech;
import org.duckdns.hjow.colonization.elements.states.ImmuneInfluenza;
import org.duckdns.hjow.colonization.elements.states.Influenza;
import org.duckdns.hjow.colonization.elements.states.SuperAngry;

/** 기본 제공 Pack */
public class BundledPack extends DefaultPack {
    private static final long serialVersionUID = -1884375631795840563L;
    @Override
    protected void init() {
        name = "표준 Pack";
        desc = "Colonization 표준 Pack";
        
        colonyClasses.add(NormalColony.class);
        
        facilityClasses.add(ResidenceModule.class);
        facilityClasses.add(PowerStation.class);
        facilityClasses.add(Restaurant.class);
        facilityClasses.add(Arcade.class);
        facilityClasses.add(Factory.class);
        facilityClasses.add(ResearchCenter.class);
        facilityClasses.add(ArchitectOffice.class);
        facilityClasses.add(BusStation.class);
        facilityClasses.add(Turret.class);
        facilityClasses.add(TownHouse.class);
        
        researchClasses.add(BasicScience.class);
        researchClasses.add(BasicHumanities.class);
        researchClasses.add(MilitaryTech.class);
        researchClasses.add(BasicBuildingTech.class);
        researchClasses.add(BasicBiology.class);
        researchClasses.add(BasicMedicalScience.class);
        researchClasses.add(BasicEngineering.class);
        
        stateClasses.add(Influenza.class);
        stateClasses.add(ImmuneInfluenza.class);
        stateClasses.add(SuperAngry.class);
        
    }
}
