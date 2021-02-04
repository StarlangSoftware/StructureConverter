package StructureConverter.DependencyToConstituency;/* Created by oguzkeremyildiz on 2.02.2021 */

import DependencyParser.UniversalDependencyRelation;
import StructureConverter.WordNodePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;

public class BasicOracle implements ProjectionOracle {

    /**
     * @param first first {@link WordNodePair}.
     * @param second second {@link WordNodePair}.
     * @param specialsMap priority {@link HashMap}.
     * @return which {@link WordNodePair} has priority based on {@link UniversalDependencyRelation}s.
     */

    private int compareTo(WordNodePair first, WordNodePair second, HashMap<String, Integer> specialsMap) {
        String firstUniversalDependency = first.getUniversalDependency();
        String secondUniversalDependency = second.getUniversalDependency();
        if (specialsMap.containsKey(firstUniversalDependency) && specialsMap.containsKey(secondUniversalDependency)) {
            return specialsMap.get(firstUniversalDependency).compareTo(specialsMap.get(secondUniversalDependency));
        } else if (specialsMap.containsKey(firstUniversalDependency)) {
            return 1;
        } else if (specialsMap.containsKey(secondUniversalDependency)) {
            return -1;
        }
        return 0;
    }

    private int addCommandForDecreasing(int index, ArrayList<WordNodePair> unionList, HashMap<String, Integer> specialsMap, ArrayList<SimpleEntry<Command, String>> commands) {
        int i = 0;
        while (index - (i + 1) > -1) {
            if (compareTo(unionList.get(index - i), unionList.get(index - (i + 1)), specialsMap) == 0) {
                commands.add(new SimpleEntry<>(Command.LEFT, null));
            } else {
                break;
            }
            i++;
        }
        return i + 1;
    }

    private int addCommandsForLeft(int currentIndex, int i, ArrayList<WordNodePair> unionList, HashMap<String, Integer> specialsMap, ArrayList<SimpleEntry<Command, String>> commands) {
        if (currentIndex - (i + 1) > -1 && compareTo(unionList.get(currentIndex - i), unionList.get(currentIndex - (i + 1)), specialsMap) == 0) {
            i += addCommandForDecreasing(currentIndex - i, unionList, specialsMap, commands);
            commands.add(new SimpleEntry<>(Command.LEFT, null));
        } else {
            commands.add(new SimpleEntry<>(Command.LEFT, null));
            i++;
        }
        commands.add(new SimpleEntry<>(Command.MERGE, null));
        return i;
    }

    private int findSpecialIndex(ArrayList<WordNodePair> unionList, int currentIndex) {
        for (int i = 0; i < unionList.size(); i++) {
            if (currentIndex != i && unionList.get(i).getUniversalDependency().equals("NSUBJ") || unionList.get(i).getUniversalDependency().equals("CSUBJ")) {
                return i;
            }
        }
        return -1;
    }

    private void addSpecialForLeft(ArrayList<WordNodePair> unionList, ArrayList<SimpleEntry<Command, String>> commands, int i, int j, int currentIndex) {
        boolean check = false;
        while (currentIndex - i > -1) {
            if (unionList.get(currentIndex - i).getWord().isPunctuation() || unionList.get(currentIndex - i).getUniversalDependency().equals("NSUBJ") || unionList.get(currentIndex - i).getUniversalDependency().equals("CSUBJ")) {
                break;
            } else {
                check = true;
                commands.add(new SimpleEntry<>(Command.LEFT, null));
            }
            i++;
        }
        while (currentIndex + j < unionList.size()) {
            if (unionList.get(currentIndex + j).getWord().isPunctuation()) {
                break;
            } else {
                check = true;
                commands.add(new SimpleEntry<>(Command.RIGHT, null));
            }
            j++;
        }
        if (check) {
            commands.add(new SimpleEntry<>(Command.MERGE, "VP"));
        }
        while (currentIndex + j < unionList.size()) {
            commands.add(new SimpleEntry<>(Command.RIGHT, null));
            j++;
        }
        while (currentIndex - i > -1) {
            commands.add(new SimpleEntry<>(Command.LEFT, null));
            i++;
        }
        commands.add(new SimpleEntry<>(Command.MERGE, null));
    }

    private boolean containsWordNodePair(ArrayList<WordNodePair> unionList, int wordNodePairNo) {
        for (WordNodePair wordNodePair : unionList) {
            if (wordNodePair.getNo() == wordNodePairNo) {
                return true;
            }
        }
        return false;
    }

    private int finalCommandsForObjects(ArrayList<WordNodePair> unionList, int currentIndex, int i, ArrayList<SimpleEntry<Command, String>> commands) {
        if (unionList.get(currentIndex).getWord().getUniversalDependency().toString().equals("ROOT")) {
            int bound = -1;
            for (int j = 0; j < unionList.size(); j++) {
                if (unionList.get(j).getTo() - 1 == unionList.get(currentIndex).getNo() && unionList.get(j).getUniversalDependency().equals("OBJ") || unionList.get(j).getUniversalDependency().equals("IOBJ") || unionList.get(j).getUniversalDependency().equals("OBL")) {
                    bound = j;
                    break;
                }
            }
            if (bound > -1) {
                boolean check = false;
                while (currentIndex - i >= bound) {
                    check = true;
                    commands.add(new SimpleEntry<>(Command.LEFT, null));
                    i++;
                }
                if (check) {
                    commands.add(new SimpleEntry<>(Command.MERGE, "VP"));
                }
            }
        }
        return i;
    }

    @Override
    public ArrayList<SimpleEntry<Command, String>> makeCommands(HashMap<String, Integer> specialsMap, ArrayList<WordNodePair> unionList, int currentIndex) {
        int i = 1, j = 1, specialIndex = -1;
        ArrayList<SimpleEntry<Command, String>> commands = new ArrayList<>();
        while (currentIndex - i > -1 || currentIndex + j < unionList.size()) {
            if (currentIndex - i > -1 && currentIndex + j < unionList.size()) {
                int comparisonResult = compareTo(unionList.get(currentIndex - i), unionList.get(currentIndex + j), specialsMap);
                if (comparisonResult > 0) {
                    i = addCommandsForLeft(currentIndex, i, unionList, specialsMap, commands);
                } else if (comparisonResult < 0) {
                    commands.add(new SimpleEntry<>(Command.RIGHT, null));
                    commands.add(new SimpleEntry<>(Command.MERGE, null));
                    j++;
                } else {
                    if (!specialsMap.containsKey(unionList.get(currentIndex - i).getUniversalDependency()) && !specialsMap.containsKey(unionList.get(currentIndex + j).getUniversalDependency())) {
                        break;
                    } else {
                        commands.add(new SimpleEntry<>(Command.LEFT, null));
                        commands.add(new SimpleEntry<>(Command.RIGHT, null));
                        commands.add(new SimpleEntry<>(Command.MERGE, null));
                        i++;
                        j++;
                    }
                }
            } else if (currentIndex - i > -1) {
                if (specialsMap.containsKey(unionList.get(currentIndex - i).getUniversalDependency())) {
                    i = addCommandsForLeft(currentIndex, i, unionList, specialsMap, commands);
                } else {
                    if (unionList.get(currentIndex - i).getUniversalDependency().equals("NSUBJ") || unionList.get(currentIndex - i).getUniversalDependency().equals("CSUBJ")) {
                        specialIndex = currentIndex - i;
                    }
                    break;
                }
            } else {
                if (specialsMap.containsKey(unionList.get(currentIndex + j).getUniversalDependency())) {
                    commands.add(new SimpleEntry<>(Command.RIGHT, null));
                    commands.add(new SimpleEntry<>(Command.MERGE, null));
                    j++;
                } else {
                    break;
                }
            }
        }
        if (specialIndex == -1) {
            specialIndex = findSpecialIndex(unionList, currentIndex);
        }
        if (specialIndex > -1 && containsWordNodePair(unionList, unionList.get(specialIndex).getTo() - 1)) {
            if (currentIndex > specialIndex) {
                addSpecialForLeft(unionList, commands, i, j, currentIndex);
            } else {
                // temporary solution
                i = finalCommandsForObjects(unionList, currentIndex, i, commands);
                boolean check = false;
                while (currentIndex + j < unionList.size()) {
                    check = true;
                    commands.add(new SimpleEntry<>(Command.RIGHT, null));
                    j++;
                }
                while (currentIndex - i > -1) {
                    check = true;
                    commands.add(new SimpleEntry<>(Command.LEFT, null));
                    i++;
                }
                if (check) {
                    commands.add(new SimpleEntry<>(Command.MERGE, null));
                }
                // temporary solution
            }
        } else {
            i = finalCommandsForObjects(unionList, currentIndex, i, commands);
            boolean check = false;
            while (currentIndex + j < unionList.size()) {
                check = true;
                commands.add(new SimpleEntry<>(Command.RIGHT, null));
                j++;
            }
            while (currentIndex - i > -1) {
                check = true;
                commands.add(new SimpleEntry<>(Command.LEFT, null));
                i++;
            }
            if (check) {
                commands.add(new SimpleEntry<>(Command.MERGE, null));
            }
        }
        return commands;
    }
}
