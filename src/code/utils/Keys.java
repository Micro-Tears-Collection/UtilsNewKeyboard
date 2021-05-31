package code.utils;

import java.io.DataOutputStream;

/**
 *
 * @author Roman Lahin
 */

public class Keys {

    public static int UP, DOWN, LEFT, RIGHT, OK, ESC;
    
    private static int[] pressed;
    private static long[] pressedTime;
    private static int pressedCount = 0;
    
    private static int[] bindings, bindedKeysCount;
    private static int bindingsCount;
    //Count is 0-2
    
    static {
        pressed = new int[10];
        pressedTime = new long[10];
        
        bindings = new int[20];
        bindedKeysCount = new int[10];
        
        UP = createBinding();
        DOWN = createBinding();
        LEFT = createBinding();
        RIGHT = createBinding();
        OK = createBinding();
        ESC = createBinding();
    }
    
    //Returns new binding ID
    private static int createBinding() {
        if(bindedKeysCount.length == bindingsCount) {
            int[] b = new int[bindings.length + 20];
            System.arraycopy(bindings, 0, b, 0, bindings.length);
            bindings = b;
            
            int[] bkc = new int[bindedKeysCount.length + 10];
            System.arraycopy(bindedKeysCount, 0, bkc, 0, bindedKeysCount.length);
            bindedKeysCount = bkc;
        }
        
        bindedKeysCount[bindingsCount] = 0;
        bindingsCount++;
        
        return (bindingsCount - 1) + 1;
    }
    
    //Zero means that binding isnt created yet
    //Returns new(or existing) binding ID
    public static int addKeyToBinding(int bindingID, int key) {
        if(bindingID == 0) bindingID = createBinding()-1;
        else bindingID--;
        
        int count = bindedKeysCount[bindingID];
        if(count >= 2) count = 0;
        
        bindings[(bindingID << 1) + count] = key;
        bindedKeysCount[bindingID] = count + 1;
        
        return bindingID + 1;
    }
    
    public static void removeKeysFromBinding(int binding) {
        if(binding == 0) return;
        
        bindedKeysCount[binding - 1] = 0;
    }
    
    public static void reset() {
        pressedCount = 0;
    }
    
    public static void keyPressed(int key) { 
        for(int i=0; i<pressedCount; i++) {
            if(pressed[i] == key) return;
        }
        
        if(pressedCount == pressed.length) {
            int[] pn = new int[pressed.length + 10];
            System.arraycopy(pressed, 0, pn, 0, pressed.length);
            pressed = pn;
            
            long[] pt = new long[pressedTime.length + 10];
            System.arraycopy(pressedTime, 0, pt, 0, pressedTime.length);
            pressedTime = pt;
        }
        
        pressed[pressedCount] = key;
        pressedTime[pressedCount] = FPS.currentTime;
        
        pressedCount++;
    }
    
    public static void keyReleased(int key) {
        int pressedID = -1;
        for(int i=0; i<pressedCount; i++) {
            if(pressed[i] == key) {
                pressedID = i; break;
            }
        }
        
        if(pressedID == -1) return;
        
        pressedCount--;
        
        if(pressedCount >= 1) {
            pressed[pressedID] = pressed[pressedCount];
            pressedTime[pressedID] = pressedTime[pressedCount];
        }
    }
    
    static long isPressedTime;
    
    public static boolean isPressed(int bindingID) {
        if(bindingID == 0|| pressedCount == 0 || bindedKeysCount[bindingID - 1] == 0) return false;
        
        bindingID--;
        
        for(int i = 0; i < pressedCount; i++) {
            int key = pressed[i];

            for(int x = 0; x < bindedKeysCount[bindingID]; x++) {
                int key2 = bindings[(bindingID << 1) + x];
                if(key == key2) {
                    isPressedTime = pressedTime[i];
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static boolean isThatBinding(int key, int bindingID) {
        if(bindingID == 0 || bindedKeysCount[bindingID - 1] == 0) return false;
        
        bindingID--;
            
        for(int x = 0; x < bindedKeysCount[bindingID]; x++) {
            int key2 = bindings[(bindingID << 1) + x];
            if(key == key2) return true;
        }
        
        return false;
    }
    
    public static int getBindingKeyCode(int bindingID, int keyID) {
        int count;
        if(bindingID == 0 || (count = bindedKeysCount[bindingID - 1]) == 0) return Integer.MIN_VALUE;
        
        bindingID--;
            
        return bindings[(bindingID << 1) + (keyID % count)];
    }
    
    public static void saveBindings(DataOutputStream dos) {
        //todo
    }
}
