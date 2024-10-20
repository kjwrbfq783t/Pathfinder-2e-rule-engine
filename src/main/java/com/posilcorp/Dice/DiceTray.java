package com.posilcorp.Dice;

import java.util.Random;

public class DiceTray {
    private static Random random=new Random();
    public static int roll(int times,Dice dicetype){
        int result=0;
        switch (dicetype) {
            case D4:
            for(int i=1;i<=times;i++){
                result+=random.nextInt(4)+1;
            }
            return result;
            case D6:
            for(int i=1;i<=times;i++){
                result+=random.nextInt(6)+1;
            }
            return result;
            case D8:
            for(int i=1;i<=times;i++){
                result+=random.nextInt(8)+1;
            }
            return result;   
            case D12:
            for(int i=1;i<=times;i++){
                result+=random.nextInt(12)+1;
            }
            return result;
            case D20:
            for(int i=1;i<=times;i++){
                result+=random.nextInt(20)+1;
            }
            return result;
            default:
            return 0;
        }
    }
}
