/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker;

import java.util.LinkedList;

/**
 *
 * @author Jaroslav Brabec
 */
public class poker_rules {

    /**
     * dostane na vstup několik karet a určí nejlepší kombinaci z nich.
     * Nejdříve karty setřídí. Používá také růzdných bitových map atd.
     * @param cards karty, ze kterých má být vybrána kombinace
     * @return nejlepší možná kombinace
     */
    public static poker_hand best_hand(Card[] cards) {
        int[] values, colors;
        int max_value = 0;
        int max_color = 0;
        int pairs = 0;
        int triples = 0;
        int straight_counter = 0;
        boolean straight = false;
        boolean[][] arr = new boolean[13][4];
        LinkedList<Card> chosed = new LinkedList<Card>();
        values = new int[13];
        colors = new int[4];
        poker_hand res;
        chosed.clear();

        for (Card c : cards) {
            values[c.getValue().toInt()]++;
            colors[c.getColor().toInt()]++;
            arr[c.getValue().toInt()][c.getColor().toInt()] = true;
        }

        for (int i = 0; i < values.length; i++) {
            if (values[i] > max_value) {
                max_value = values[i];
            }
            if (values[i] == 2) {
                pairs++;
            }
            if (values[i] == 3) {
                triples++;
            }
            if (values[i] > 0) {
                straight_counter++;
            } else {
                straight_counter = 0;
            }
            if (straight_counter == 5) {
                straight = true;
            }
        }

        for (int i = 0; i < 4; i++) {
            if (colors[i] > max_color) {
                max_color = colors[i];
            }
        }


        if (max_color >= 5 && straight) { //otestujem straight flush
            int flush = 0;
            boolean straight_flush = false;
            for (int i = 0; i < 4; i++) {
                if (colors[i] >= 5) {
                    flush = i;
                }
            }
            int str = 0;
            for (int i = values.length-1; i >= 0; i--) {
                if (arr[i][flush]) {
                    str++;
                    chosed.add(new Card(i, flush));
                } else {
                    str = 0;
                    chosed.clear();
                }
                if (str == 5) {
                    straight_flush = true;
                    break;
                }
            }
            if (straight_flush) {
                return new poker_hand(poker_combinations.Straight_Flush, chosed.toArray(new Card[chosed.size()]));
            }
            chosed.clear();
        }
        if (max_value == 4) {
            for (int i = values.length-1; i >= 0; i--) {
                if (values[i] == 4) {
                    chosed.add(new Card(i, 0));
                    chosed.add(new Card(i, 1));
                    chosed.add(new Card(i, 2));
                    chosed.add(new Card(i, 3));
                    values[i] = 0;
                    break;
                }
            }
            for (int i = values.length - 1; i >= 0; i--) {
                if (values[i] > 0) {
                    for (int j = 0; j < 4; j++) {
                        if (arr[i][j]) {
                            chosed.add(new Card(i, j));
                            return new poker_hand(poker_combinations.Four_of_Kind, chosed.toArray(new Card[chosed.size()]));
                        }
                    }
                }
            }
        }
        if (triples >= 2 || (triples == 1 && pairs > 0)) {
            for (int i = values.length-1; i >= 0; i--) {
                if (values[i] == 3) {
                    for (int j = 0; j < 4; j++) {
                        if (arr[i][j]) {
                            chosed.add(new Card(i, j));
                        }
                    }
                    values[i] = 0;
                    break;
                }
            }
            for (int i = values.length-1; i >= 0; i--) {
                if (values[i] >= 2) {
                    int val = 0;
                    for (int j = 0; j < 4; j++) {
                        if (arr[i][j]) {
                            val++;
                            chosed.add(new Card(i, j));
                            if (val == 2) {
                                break;
                            }
                        }
                    }
                    return new poker_hand(poker_combinations.Full_House, chosed.toArray(new Card[chosed.size()]));
                }
            }
        }
        if (max_color >= 5) {
            int flush = 0;
            int count = 0;
            for (int i = 0; i < 4; i++) {
                if (colors[i] >= 5) {
                    flush = i;
                }
            }
            for (int i = values.length - 1; i >= 0; i--) {
                if (arr[i][flush]) {
                    count++;
                    chosed.add(new Card(i, flush));
                }
                if (count == 5) {
                    break;
                }
            }
            return new poker_hand(poker_combinations.Flush, chosed.toArray(new Card[chosed.size()]));
        }
        if (straight) {
            int str = 0;
            for (int i = values.length - 1; i >= 0; i--) {
                if (values[i] > 0) {
                    str++;
                    for (int j = 0; j < 4; j++) {
                        if (arr[i][j]) {
                            chosed.add(new Card(i, j));
                            break;
                        }
                    }
                } else {
                    str = 0;
                    chosed.clear();
                }
                if (str == 5) {
                    break;
                }
            }
            return new poker_hand(poker_combinations.Straight, chosed.toArray(new Card[chosed.size()]));
        }
        if (max_value >= 3) {
            for (int i = values.length - 1; i >= 0; i--) {
                if (values[i] == 3) {
                    for (int j = 0; j < 4; j++) {
                        if (arr[i][j]) {
                            chosed.add(new Card(i, j));
                        }
                    }
                    values[i] = 0;
                    break;
                }
            }

            for (int k = 0; k < 2; k++) {
                for (int i = values.length - 1; i >= 0; i--) {
                    if (values[i] > 0) {
                        for (int j = 0; j < 4; j++) {
                            if (arr[i][j]) {
                                chosed.add(new Card(i, j));
                                values[i]--;
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            return new poker_hand(poker_combinations.Three_Of_Kind, chosed.toArray(new Card[chosed.size()]));
        }

        if (pairs >= 2) {
            for (int k = 0; k < 2; k++) {
                for (int i = values.length - 1; i >= 0; i--) {
                    if (values[i] == 2) {
                        for (int j = 0; j < 4; j++) {
                            if (arr[i][j]) {
                                arr[i][j]=false;
                                chosed.add(new Card(i, j));
                                values[i]--;
                            }
                        }
                        values[i] = 0;
                    }
                }
            }
            for (int i = values.length - 1; i >= 0; i--) {
                if (values[i] > 0) {
                    for (int j = 0; j < 4; j++) {
                        if (arr[i][j]) {
                            chosed.add(new Card(i, j));
                            return new poker_hand(poker_combinations.Two_Pair, chosed.toArray(new Card[chosed.size()]));
                        }
                    }
                }
            }
        }
        if (pairs > 0) {
            for (int i = values.length - 1; i >= 0; i--) {
                if (values[i] == 2) {
                    for (int j = 0; j < 4; j++) {
                        if (arr[i][j]) {
                            chosed.add(new Card(i, j));
                            values[i]--;
                            arr[i][j]=false;
                        }
                    }
                    values[i] = 0;
                    break;
                }
            }
            int count = 2;
            for (int i = values.length - 1; i >= 0; i--) {
                for (int j = 0; j < 4; j++) {
                    if (arr[i][j]) {
                        count++;
                        chosed.add(new Card(i, j));
                    }
                    if (count == 5) {
                        break;
                    }
                }
                if (count == 5) {
                    break;
                }

            }
            return new poker_hand(poker_combinations.One_Pair, chosed.toArray(new Card[chosed.size()]));
        }
        //high card
        {
            int count = 0;
            for (int i = values.length - 1; i >= 0; i--) {
                for (int j = 0; j < 4; j++) {
                    if (arr[i][j]) {
                        count++;
                        chosed.add(new Card(i, j));
                    }
                    if (count == 5) {
                        break;
                    }
                }
                if (count == 5) {
                    break;
                }
            }
            return new poker_hand(poker_combinations.High_Card, chosed.toArray(new Card[chosed.size()]));
        }

    }
}
