package net.dinomine.potioneer.util;

import net.dinomine.potioneer.beyonder.abilities.Ability;

import java.util.Map;

public class OtherStuffToDelete {


//    /**
//     * Method that updates the data in newMap into original. it overrides in original the values it can get from newMap.
//     * Any extras in original remain the same, and any extra in newMap are ignored.
//     *
//     * Also guarantees that any active-type ability (defined manually per ability) is enabled.
//     * @param original
//     * @param newMap
//     * @param <T>
//     */
//    public <T> void setMap(Map<String, T> original, Map<String, T> newMap){
//
//        for (Map.Entry<String, T> entry : newMap.entrySet()) {
//            if (original.containsKey(entry.getKey())) {
//                original.put(entry.getKey(), entry.getValue());
//            }
//        }
//
//        if(newMap.size() < pathwayAbilities.size()){
//            System.out.println("WARNING: received list is SMALLER than the pathwayActives list.\nProceed with caution!!!");
////            System.out.println("List size: " + list.size());
////            System.out.println("EnabledDisabled list size: " + enabledDisabled.size());
////            System.out.println("pathwayActives list size: " + pathwayActives.size());
//        }
//        for (Ability abilityInfo: pathwayAbilities.values()) {
//            if(abilityInfo.isActive) enabledDisabled.put(abilityInfo.getInfo().ablId(), true);
//        }
//    }
}
