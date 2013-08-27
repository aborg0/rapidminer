/*
 * 
 */
package com.rapidminer.gui;

import java.awt.Window;

/**
 * This interface contains the tutorial related actions.
 * 
 * @author Gábor Bakos
 */
public interface TutorialState {

	public void startTutorial();

	public void setTutorialMode(boolean mode);

	public boolean isTutorialMode();

	public Window getWindow();

}