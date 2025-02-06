package src;

import src.UI.UIHandler;
import src.enums.WindowType;

public class Main {

        public static void main(String[] args) {
            HandDetection handDetection = new HandDetection();

            UIHandler uiHandler = new UIHandler(handDetection,handDetection);
            uiHandler.showWindow(WindowType.MAIN);
        }
    }
