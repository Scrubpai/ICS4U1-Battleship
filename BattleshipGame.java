// Battleship Main Program
// Authors: Louis Sun, Andy Li, Fergus Chui
// ICS 4U1
// June 16, 2022
// Version 7.27

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class BattleshipGame implements ActionListener, MouseListener, MouseMotionListener {
	// Properties
	Font font1 = new Font("SansSerif", Font.BOLD, 20);
	JFrame theFrame = new JFrame("Battleship");
	Timer theTimer = new Timer(1000/60, this);
	Timer animTimer = new Timer(1000/10, this); // For animation
	SuperSocketMaster ssm;
	
	// Home Panel
	BattleshipHomePanel homePanel = new BattleshipHomePanel();
	JTextField joinIP = new JTextField();
	JButton hostButton = new JButton("Host Game");
	JButton joinButton = new JButton("Join Game");
	JButton helpButton = new JButton("Help");
	JButton quitButton = new JButton("Quit");
	JButton themesButton = new JButton("Themes");
	
	// Play Panel
	BattleshipPlayPanel playPanel = new BattleshipPlayPanel();
	JButton rotateButton = new JButton("Rotate");
	JButton readyButton = new JButton("Ready");
	JTextField yourMsgField = new JTextField();
	JTextField opponentMsgField = new JTextField();
	
	// Help Panel
	BattleshipHelpPanel helpPanel = new BattleshipHelpPanel();
	BattleshipHelpPanel2 helpPanel2 = new BattleshipHelpPanel2();
	JButton nextButton1 = new JButton("Next");
	JButton nextButton2 = new JButton("Next");
	JButton nextButton3 = new JButton("Next");
	JButton nextButton4 = new JButton("Next");
	JButton nextButton5 = new JButton("Next");
	JButton nextButton6 = new JButton("Next");
	JButton nextButton7 = new JButton("Next");
	JTextField step1 = new JTextField("Demo Step 1: Drag and Drop your ships onto your map");
	JTextField step1rotate = new JTextField("Click the rotate button BEFORE PLACING YOUR SHIPS to rotate them");
	JTextField step1ready = new JTextField("Click the ready button when you've placed all 5 ships");
	JButton step1rotateButton = new JButton("Rotate");
	JButton step1readyButton = new JButton("Ready");
	JTextField step1yourMsgField = new JTextField();
	JTextField step1opponentMsgField = new JTextField();
	JTextField yourmsgfield = new JTextField("This top chat area displays YOUR messages");
	JTextField opponentmsgfield = new JTextField("This bottom chat area displays OPPONENT messages");
	JTextField bothready = new JTextField("Once both players are ready, your ships get moved to the minimap");
	JTextField step2 = new JTextField("Demo Step 2: Guess where your opponent's ships are located by clicking on a blue square/coordinate");
	JTextField step2info = new JTextField("You take turns guessing"); 
	JTextField step2hit = new JTextField("Try clicking on a square to see what a hit looks like! If you hit, you go again!");
	JTextField step2hitinfo = new JTextField("Once a ship has been sunk, it will show up on the screen (not in demo)");
	JTextField step2miss = new JTextField("Try clicking on a square to see what a miss looks like!");
	JTextField step2missinfo = new JTextField("Notice that after you have guessed a square, you can't click it again");
	
	//Themes Panel
	BattleshipThemesPanel themesPanel = new BattleshipThemesPanel();
	JTextField themesField = new JTextField();
	JButton homeButton = new JButton("Back Home");
	JRadioButton themes1Button = new JRadioButton(themesPanel.strThemes[0]);
	JRadioButton themes2Button = new JRadioButton(themesPanel.strThemes[1]);
	JRadioButton themes3Button = new JRadioButton(themesPanel.strThemes[2]);
	JRadioButton themes4Button = new JRadioButton(themesPanel.strThemes[3]);
	ButtonGroup buttonGroup;
	JButton confirmButton = new JButton("Confirm");
	JLabel themesLabel;
		
	// Methods
	public void actionPerformed(ActionEvent evt){
		if(evt.getSource() == theTimer && playPanel.blnPlayingAnimation == false) { // Only one timer will repaint at a time
			if (theFrame.getContentPane() == playPanel) {
				playPanel.repaint();
			} else if (theFrame.getContentPane() == helpPanel2) {
				helpPanel2.repaint();
			}
		} else if (evt.getSource() == animTimer) {
			if (theFrame.getContentPane() == playPanel) {
				for (int intAnimation=1; intAnimation<=3; intAnimation++) {
					if (playPanel.blnPlayAnimation[intAnimation] == true) { // Draw the current frame of the animation
						playPanel.repaint();
						playPanel.intAnimCount++;
							
						if(playPanel.intAnimCount > playPanel.intMaxAnimationSprites[intAnimation]){ // Animation has finished
							playPanel.blnPlayAnimation[intAnimation] = false;
							playPanel.intAnimCount = 0;
						}
						
						break; // So it doesn't play 2 animations at the same time
					}
				}
				
				if (playPanel.blnPlayAnimation[1] == false && playPanel.blnPlayAnimation[2] == false && playPanel.blnPlayAnimation[3] == false) {
					playPanel.blnPlayingAnimation = false; // No more animations are playing
					
					if (playPanel.intSunkValue != 0) {
						playPanel.intOpponentGrid[playPanel.intSunkRow][playPanel.intSunkCol] = playPanel.intSunkValue;
						playPanel.intSunkRow = playPanel.intSunkCol = playPanel.intSunkValue = 0;
					}
				}
			} else if (theFrame.getContentPane() == helpPanel2) {
				for (int intAnimation=1; intAnimation<=3; intAnimation++) {
					if (helpPanel2.blnPlayAnimation[intAnimation] == true) { // Draw the current frame of the animation
						helpPanel2.repaint();
						helpPanel2.intAnimCount++;
							
						if(helpPanel2.intAnimCount > helpPanel2.intMaxAnimationSprites[intAnimation]) { // Animation has finished
							helpPanel2.blnPlayAnimation[intAnimation] = false;
							helpPanel2.intAnimCount = 0;
						}
						
						break; // So it doesn't play 2 animations at the same time
					}
				}
				
				if (helpPanel2.blnPlayAnimation[1] == false && helpPanel2.blnPlayAnimation[2] == false && helpPanel2.blnPlayAnimation[3] == false) {
					helpPanel2.blnPlayingAnimation = false; // No more animations are playing
				}
			}
			
		}
		else if (evt.getSource() == hostButton) { // Host the game
			ssm = new SuperSocketMaster(9001, this);
			ssm.connect();
			playPanel.blnYourTurn = true; // Server goes first
			
			theFrame.setContentPane(playPanel);
			theFrame.pack();
		} else if (evt.getSource() == joinButton) { // Join the game
			String strText = joinIP.getText();
			ssm = new SuperSocketMaster(strText, 9001, this);
			ssm.connect();
			
			theFrame.setContentPane(playPanel);
			theFrame.pack();
		} else if(evt.getSource() == quitButton) { // Quit
			System.out.println("Quit");
			System.exit(1);			
		} else if (evt.getSource() == rotateButton) { // Rotate the ships on the wooden box/board (they haven't been placed yet)
			playPanel.blnHorizontal = !playPanel.blnHorizontal;
			
			for (int intShip=1; intShip<=5; intShip++) {
				if (playPanel.intPlaced[intShip] == 0) { // Ship has not been placed yet
					if (playPanel.blnHorizontal == false) { // Update the positions which the ships are drawn to  on the wooden board (vertical)
						playPanel.intPositions[intShip][0] = playPanel.intDefaultPositionsV[intShip][0];
						playPanel.intPositions[intShip][1] = playPanel.intDefaultPositionsV[intShip][1];
					} else { // Update the positions which the ships are drawn to on the wooden board (horizontal)
						playPanel.intPositions[intShip][0] = playPanel.intDefaultPositionsH[intShip][0];
						playPanel.intPositions[intShip][1] = playPanel.intDefaultPositionsH[intShip][1];	
					}
				}
			}
		} else if (evt.getSource() == readyButton) { // Player is ready
			if (playPanel.intPlaced[1] == 0 || playPanel.intPlaced[2] == 0 || playPanel.intPlaced[3] == 0 || playPanel.intPlaced[4] == 0 || playPanel.intPlaced[5] == 0) {
				// If any ship has not been placed yet, the player cannot be ready
				return;
			}
			
			ssm.sendText("READY");
			yourMsgField.setText("READY");
			
			if (opponentMsgField.getText().equals("READY")) { // Both players are ready, start game
				playPanel.blnStartGame = true;
				System.out.println(playPanel.blnStartGame);
				System.out.println(playPanel.blnYourTurn);
			}
		} else if (evt.getSource() == ssm) {
			String strText = ssm.readText();
			System.out.println("THEMSG: " + strText);
			if (strText.equals("READY")) { // Both players are ready, start game
				opponentMsgField.setText("READY");
				if (yourMsgField.getText().equals("READY")) {
					playPanel.blnStartGame = true;
				}
			} else if (strText.equals("HIT")) { // You have guessed a hit on (intRow, intCol)
				playPanel.blnHit = true;
				playPanel.blnYourTurn = true; // You go again
				playPanel.blnPlayAnimation[2] = true; // Explosion Animation
				opponentMsgField.setText("HIT");
				String strLocation = yourMsgField.getText();
				int intCol = strLocation.charAt(0) - 'A' + 1;
				int intRow = Integer.parseInt(strLocation.substring(1, 2));
				if (strLocation.length() == 3) {
					intRow *= 10;
				}
				
				// Update map
				playPanel.intOpponentGrid[intRow][intCol] = 1; // 1 = Hit
			} else if (strText.equals("MISS")) { // You have guessed a miss on (intRow, intCol)
				playPanel.blnHit = false;
				playPanel.blnYourTurn = false; // Opponent's turn now
				playPanel.blnPlayAnimation[3] = true; // Splash
				opponentMsgField.setText("MISS");
				
				String strLocation = yourMsgField.getText();
				int intCol = strLocation.charAt(0) - 'A' + 1;
				int intRow = Integer.parseInt(strLocation.substring(1, 2));
				if (strLocation.length() == 3) {
					intRow *= 10;
				}
				
				// Update map
				playPanel.intOpponentGrid[intRow][intCol] = 2; // 2 = Miss
			} else if (strText.length() >= 4 && strText.substring(0, 4).equals("Sunk")) { // You have sunk an opponent's ship
				playPanel.blnHit = true; // Also hit
				playPanel.blnYourTurn = true;
				playPanel.blnPlayAnimation[2] = true; // Explosion Animation
				opponentMsgField.setText("HIT");
				String strLocation = yourMsgField.getText();
				int intCol = strLocation.charAt(0) - 'A' + 1;
				int intRow = Integer.parseInt(strLocation.substring(1, 2));
				if (strLocation.length() == 3) {
					intRow *= 10;
				}
				// Update map
				playPanel.intOpponentGrid[intRow][intCol] = 1; // 1 = Hit
				
				String strSub = strText.substring(5);
				String strArray[] = strSub.split(" ");
				int intShip = Integer.parseInt(strArray[0]); // The ship that you have sunk
				int intSunkRow = Integer.parseInt(strArray[1]); // The row location of the ship that you have sunk
				int intSunkCol = Integer.parseInt(strArray[2]); // The column location of the ship that you have sunk
				int intOrientation = Integer.parseInt(strArray[3]); // 1 - Vertical, 2 - Horizontal
				System.out.println(strSub + " " + intShip + " " + intSunkRow + " " + intSunkCol + " " + intOrientation);
				
				playPanel.intSunkRow = intSunkRow;
				playPanel.intSunkCol = intSunkCol;
				playPanel.intSunkValue = intShip * 10 + intOrientation; // Calculate and update value of intOpponentGrid[][]
			} else if (strText.equals("You Win")) { // YOU WIN
				playPanel.intWinLose = 1;
				yourMsgField.setText("You win");
				opponentMsgField.setText("You lose");
			} else { // Opponent's guess
				opponentMsgField.setText(strText);
				// Opponent guessed the coordinate (intRow, intCol)
				int intCol = strText.charAt(0) - 'A' + 1;
				int intRow = Integer.parseInt(strText.substring(1, 2));
				if (strText.length() == 3) {
					intRow *= 10;
				}
				
				if (playPanel.intYourGrid[intRow][intCol] >= 1 && playPanel.intYourGrid[intRow][intCol] <= 5) { // A ship is located at the coordinate which your opponent guessed
					int intShip = playPanel.intYourGrid[intRow][intCol];
					playPanel.intYourGrid[intRow][intCol] *= 10; // Set it to be hit
					playPanel.intShipHits[intShip]++; // Increment the number of times the ship is hit
					if(playPanel.intSizes[intShip] == playPanel.intShipHits[intShip]) { // Ship has been sunk
						playPanel.intShipsSunk++;
						
						// Position of the ship which is sunk
						int intSunkRow = (int)Math.floor(1.0 * (playPanel.intPositions[intShip][1] - 80) / 64) + 1;
						int intSunkCol = (int)Math.floor(1.0 * (playPanel.intPositions[intShip][0] - 80) / 64) + 1;
						System.out.println("Sunk "+ intShip + " " + intSunkRow + " " + intSunkCol + " " + playPanel.intPlaced[intShip]);
						ssm.sendText("Sunk "+ intShip + " " + intSunkRow + " " + intSunkCol + " " + playPanel.intPlaced[intShip]);
						yourMsgField.setText("HIT");
						
						if(playPanel.intShipsSunk >= 5)	{ // All 5 of your ships have been sunk, you lose
							ssm.sendText("You Win");
							yourMsgField.setText("You Lose");
							opponentMsgField.setText("You Win");
							playPanel.intWinLose = 2;
						}
					} else { // Ship has not been sunk, simply a hit
						ssm.sendText("HIT");
						yourMsgField.setText("HIT");
					}
				} else { // A ship is not located at the coordinate which your opponent guessed
					playPanel.intYourGrid[intRow][intCol] = -1; // 1 Means Miss
					ssm.sendText("MISS");
					yourMsgField.setText("MISS");
					playPanel.blnYourTurn = true;
				}
			}
		} else if(evt.getSource() == helpButton) { // Goes to Help Panel 1
			theFrame.setContentPane(helpPanel);
			theFrame.pack();
			helpPanel.repaint();
		} else if (evt.getSource() == nextButton1) { // Goes to Help Panel 2
			theFrame.setContentPane(helpPanel2);
			theFrame.pack();
			helpPanel2.repaint();
		} else if (evt.getSource() == step1rotateButton) { // Rotates the ship in the help panel
			if (helpPanel2.intPositionX == 800 && helpPanel2.intPositionY == 100) {
				helpPanel2.blnHorizontal = !helpPanel2.blnHorizontal;
			}
		} else if (evt.getSource() == step1readyButton) { // Sets the textfield to be ready in the help panel
			if (helpPanel2.intPositionX != 800 && helpPanel2.intPositionY != 100) {
				step1yourMsgField.setText("READY");
			}
		} else if (evt.getSource() == nextButton2) { // Help panel updates
			step1.setVisible(false);
			step1rotate.setVisible(false);
			step1ready.setVisible(false);
			nextButton2.setVisible(false);
			yourmsgfield.setVisible(true);
			opponentmsgfield.setVisible(true);
			nextButton3.setVisible(true);
		} else if (evt.getSource() == nextButton3) { // Help panel updates
			yourmsgfield.setVisible(false);
			opponentmsgfield.setVisible(false);
			nextButton3.setVisible(false);
			bothready.setVisible(true);
			step1opponentMsgField.setText("READY");
			helpPanel2.blnReady = true;
			nextButton4.setVisible(true);
		} else if (evt.getSource() == nextButton4) { // Help panel updates
			bothready.setVisible(false);
			nextButton4.setVisible(false);
			nextButton5.setVisible(true);
			step2.setVisible(true);
			step2info.setVisible(true);
		} else if (evt.getSource() == nextButton5) { // Help panel updates
			nextButton5.setVisible(false);
			step2.setVisible(false);
			step2info.setVisible(false);
			helpPanel2.blnClickHit = true;
			nextButton6.setVisible(true);
			step2hit.setVisible(true);
			step2hitinfo.setVisible(true);
		} else if (evt.getSource() == nextButton6) { // Help panel updates
			helpPanel2.blnClickHit = false;
			helpPanel2.blnClickMiss = true;
			nextButton6.setVisible(false);
			step2hit.setVisible(false);
			step2hitinfo.setVisible(false);
			step2miss.setVisible(true);
			step2missinfo.setVisible(true);
			nextButton7.setVisible(true);
		} else if (evt.getSource() == nextButton7) { // No more screens for the help panel, go back to home panel
			// Reset everything
			for (int intRow=1; intRow<=10; intRow++) {
				for (int intCol=1; intCol<=10; intCol++) {
					helpPanel2.intGrid[intRow][intCol] = 0;
				}
			}
			helpPanel2.blnClickMiss = false;
			step2miss.setVisible(false);
			step2missinfo.setVisible(false);
			nextButton7.setVisible(false);
			helpPanel2.intPositionX = 800;
			helpPanel2.intPositionY = 100;
			helpPanel2.blnSelected = false;
			helpPanel2.blnHorizontal = false;
			helpPanel2.blnReady = false;
			helpPanel2.blnClickHit = false;
			helpPanel2.blnClickMiss = false;
			step1.setVisible(true);
			step1rotate.setVisible(true);
			step1ready.setVisible(true);
			nextButton2.setVisible(true);
			step1yourMsgField.setText("");
			step1opponentMsgField.setText("");
			theFrame.setContentPane(homePanel);
			theFrame.pack();
			homePanel.repaint();
		} else if(evt.getSource() == themesButton){ // Go to select a theme
			theFrame.setContentPane(themesPanel);
			theFrame.pack();
			themesPanel.repaint();
		}else if(evt.getSource() == homeButton){ // Go back home
			theFrame.setContentPane(homePanel);
			theFrame.pack();
			homePanel.repaint();
		} else if(evt.getSource() == confirmButton){ // Confirm the theme
			if(themes1Button.isSelected()){ // Battleship theme
				playPanel.intTheme = 0;
				themesLabel.setText(themesPanel.strThemes[0] + " theme is selected");
			}else if(themes2Button.isSelected()){ // Ducky theme
				playPanel.intTheme = 1;
				themesLabel.setText(themesPanel.strThemes[1] + " theme is selected");
			}else if(themes3Button.isSelected()){ // Lego theme
				playPanel.intTheme = 2;
				themesLabel.setText(themesPanel.strThemes[2] + " theme is selected");
			}else if(themes4Button.isSelected()){ // User created theme
				playPanel.intTheme = 3;
				themesLabel.setText(themesPanel.strThemes[3] + " theme is selected");
			}
		}
		
	}
	
	public void mouseDragged(MouseEvent evt) {
		if (theFrame.getContentPane() == playPanel) {
			if (playPanel.intShipSelected == 0) { // A ship has not been selected, so nothing is dragged
				return;
			}
			
			// Move the ship along with your mouse cursor
			playPanel.intPositions[playPanel.intShipSelected][0] = evt.getX() - 32;
			playPanel.intPositions[playPanel.intShipSelected][1] = evt.getY() - 32;
		} else if (theFrame.getContentPane() == helpPanel2) {
			if (helpPanel2.blnSelected == true) { // If the ship is selected in the help panel, move the ship along with your cursor
				helpPanel2.intPositionX = evt.getX() - 32;
				helpPanel2.intPositionY = evt.getY() - 32;
			}
		}
	}
	
	public void mouseMoved(MouseEvent evt) {
	}
	
	public void mouseClicked(MouseEvent evt) {
		if (theFrame.getContentPane() == playPanel) {
			// Calculate the coordinate which you have clicked on the map
			int intRow = calcRow(evt.getY());
			int intCol = calcRow(evt.getX());
			System.out.println(intRow + " " + intCol);
			
			if (playPanel.blnStartGame == true && playPanel.blnPlayingAnimation == false && playPanel.blnGameOver == true) {
				// If the game is over and you clicked the home icon on the victory/defeat image, then it quits the game
				if (evt.getX() > 830 && evt.getX() < 980 && evt.getY() > 177 && evt.getY() < 317) {
					System.exit(1);
				}
			}
			
			if (intRow >= 1 && intRow <= 10 && intCol >= 1 && intCol <= 10 && playPanel.blnYourTurn == true && playPanel.blnStartGame == true && playPanel.blnPlayingAnimation == false) { // If it is your turn to guess and you have clicked an actual coordinate on the grid
				if (playPanel.intOpponentGrid[intRow][intCol] != 0 || playPanel.blnGameOver == true) {
					// If you have guessed the coordinate before, or if the game is already over, nothing happens
					return;
				}
				
				// Valid guess
				String strLetter = playPanel.strLetters[intCol];
				String strNumber = Integer.toString(intRow);
				
				playPanel.blnPlayingAnimation = true; // Play an animation on (intRow, intCol)
				playPanel.intAnimationRow = intRow;
				playPanel.intAnimationCol = intCol;
				playPanel.blnPlayAnimation[1] = true; // Drop Bomb Animation
				
				yourMsgField.setText(strLetter + strNumber);
				ssm.sendText(strLetter + strNumber);
			}
		} else if (theFrame.getContentPane() == helpPanel2 && helpPanel2.blnClickHit == true) { // For demo-ing the hit animations in the help Panel (same idea as the play panel)
			System.out.println("here");
			int intRow = calcRow(evt.getY());
			int intCol = calcRow(evt.getX());
			
			if (intRow >= 1 && intRow <= 10 && intCol >= 1 && intCol <= 10 && helpPanel2.blnPlayingAnimation == false) {
				if (helpPanel2.intGrid[intRow][intCol] != 0) {
					return;
				}
				helpPanel2.intGrid[intRow][intCol] = 1;
				String strLetter = helpPanel2.strLetters[intCol];
				String strNumber = Integer.toString(intRow);
				
				helpPanel2.blnPlayingAnimation = true;
				helpPanel2.intAnimationRow = intRow;
				helpPanel2.intAnimationCol = intCol;
				helpPanel2.blnPlayAnimation[1] = true;
				helpPanel2.blnPlayAnimation[2] = true;
				
				step1yourMsgField.setText(strLetter + strNumber);
				step1opponentMsgField.setText("HIT");
			}
		} else if (theFrame.getContentPane() == helpPanel2 && helpPanel2.blnClickMiss == true) { // For demo-ing the miss animations in the help Panel (same idea as the play panel)
			int intRow = calcRow(evt.getY());
			int intCol = calcRow(evt.getX());
			
			if (intRow >= 1 && intRow <= 10 && intCol >= 1 && intCol <= 10 && helpPanel2.blnPlayingAnimation == false) {
				if (helpPanel2.intGrid[intRow][intCol] != 0) {
					return;
				}
				helpPanel2.intGrid[intRow][intCol] = 2;
				String strLetter = helpPanel2.strLetters[intCol];
				String strNumber = Integer.toString(intRow);
				
				helpPanel2.blnPlayingAnimation = true;
				helpPanel2.intAnimationRow = intRow;
				helpPanel2.intAnimationCol = intCol;
				helpPanel2.blnPlayAnimation[1] = true;
				helpPanel2.blnPlayAnimation[3] = true;
				
				step1yourMsgField.setText(strLetter + strNumber);
				step1opponentMsgField.setText("MISS");
			}
		}
	}
	
	public void mouseEntered(MouseEvent evt) {
	}
	
	public void mouseExited(MouseEvent evt) {
	}
	
	public void startGame() {
		
	}
	
	public void mousePressed(MouseEvent evt) { // For grabbing onto the ships when dragging and dropping them onto the map
		if (theFrame.getContentPane() == playPanel) {
			for (int intShip=1; intShip<=5; intShip++) {
				if (playPanel.intPlaced[intShip] == 0) { // If the ship has not been placed yet
					
					// Calculates if your cursor is inside the click area of the ship
					int intX = evt.getX();
					int intY = evt.getY();
					if (playPanel.blnHorizontal == false) { // Vertical ship
						int intC1 = playPanel.intDefaultPositionsV[intShip][0];
						int intR1 = playPanel.intDefaultPositionsV[intShip][1];
						int intC2 = playPanel.intDefaultPositionsV[intShip][2];
						int intR2 = playPanel.intDefaultPositionsV[intShip][3];
						if (intX>=intC1 && intX<=intC2 && intY>=intR1 && intY<=intR2) {
							playPanel.intShipSelected = intShip;
							playPanel.intPlaced[playPanel.intShipSelected] = 1;	
							break;
						}
					} else { // Horizontal ship
						int intC1 = playPanel.intDefaultPositionsH[intShip][0];
						int intR1 = playPanel.intDefaultPositionsH[intShip][1];
						int intC2 = playPanel.intDefaultPositionsH[intShip][2];
						int intR2 = playPanel.intDefaultPositionsH[intShip][3];
						if (intX>=intC1 && intX<=intC2 && intY>=intR1 && intY<=intR2) {
							playPanel.intShipSelected = intShip;
							playPanel.intPlaced[playPanel.intShipSelected] = 2;
							break;
						}
					}
				}
			}
		} else if (theFrame.getContentPane() == helpPanel2) { // Demo-ing the drag and drop for the help panel (same idea as play panel)
			System.out.println("HERE");
			int intX = evt.getX();
			int intY = evt.getY();
			if (helpPanel2.blnHorizontal == false && helpPanel2.intPositionX == 800 && helpPanel2.intPositionY == 100 && intX >= 800 && intX <= 864 && intY >= 100 && intY <= 292) {
				helpPanel2.blnSelected = true; // The ship has been selected (grabbed)
			} else if (helpPanel2.blnHorizontal == true && helpPanel2.intPositionX == 800 && helpPanel2.intPositionY == 100 && intX >= 800 && intX <= 992 && intY >= 100 && intY <= 164) {
				helpPanel2.blnSelected = true; // The ship has been selected (grabbed)
			}
		}
	}
	
	public void mouseReleased(MouseEvent evt) { // Drops the ship onto the map
		if (theFrame.getContentPane() == playPanel) {
			int intRow = calcRow(evt.getY());
			int intCol = calcCol(evt.getX());
			int intSize = playPanel.intSizes[playPanel.intShipSelected];
			
			System.out.println(intRow + " " + intCol + " " + intSize);
			
			if (intRow < 1 || intRow > 10 || intCol < 1 || intCol > 10 || (playPanel.blnHorizontal == false && intRow + intSize - 1 > 10) || (playPanel.blnHorizontal == true && intCol + intSize - 1 > 10) || !isPossible(intRow, intCol, intSize, playPanel.blnHorizontal)) {
				// You cannot place the ship here. It is either out of bounds, or another ship already occupies a coordinate
				playPanel.intPlaced[playPanel.intShipSelected] = 0;
				
				// Puts the ship back into its default position, so the player can drag and drop it again
				if (playPanel.blnHorizontal == false) {
					playPanel.intPositions[playPanel.intShipSelected][0] = playPanel.intDefaultPositionsV[playPanel.intShipSelected][0];
					playPanel.intPositions[playPanel.intShipSelected][1] = playPanel.intDefaultPositionsV[playPanel.intShipSelected][1];					
				} else {
					playPanel.intPositions[playPanel.intShipSelected][0] = playPanel.intDefaultPositionsH[playPanel.intShipSelected][0];
					playPanel.intPositions[playPanel.intShipSelected][1] = playPanel.intDefaultPositionsH[playPanel.intShipSelected][1];	
				}
				return;
			}
			
			// Place the ship and update the position of the image
			placeShip(intRow, intCol, intSize, playPanel.blnHorizontal, playPanel.intShipSelected);
			playPanel.intPositions[playPanel.intShipSelected][0] = (intCol - 1) * 64 + 80;
			playPanel.intPositions[playPanel.intShipSelected][1] = (intRow - 1) * 64 + 80;
			playPanel.intShipSelected = 0;
		} else if (theFrame.getContentPane() == helpPanel2) { // Drops the ship onto the map (same idea as the play panel)
			int intRow = calcRow(evt.getY());
			int intCol = calcCol(evt.getX());
			
			if (helpPanel2.blnSelected == true && (intRow < 1 || intRow > 10 || intCol < 1 || intCol > 10 || (helpPanel2.blnHorizontal == false && intRow + 3 - 1 > 10) || (helpPanel2.blnHorizontal == true && intCol + 3 - 1 > 10))) {
				// The ship has not been placed onto the map (out of bounds)
				helpPanel2.intPositionX = 800;
				helpPanel2.intPositionY = 100;
			} else if (helpPanel2.blnSelected == true) {
				// The ship has been placed out the map
				helpPanel2.intPositionX = (intCol - 1) * 64 + 80;
				helpPanel2.intPositionY = (intRow - 1) * 64 + 80;
			}
			
			helpPanel2.blnSelected = false;
		}
		
	}
	
	public boolean isPossible(int intRow, int intCol, int intSize, boolean blnHorizontal) {
		// Checks if it is possible to place the ship in this location
		// Returns false if another ship already occupies the location
		if (blnHorizontal == false) {
			for (int intCount=0; intCount<intSize; intCount++) {
				if (playPanel.intYourGrid[intRow+intCount][intCol] != 0) {
					System.out.println(playPanel.intYourGrid[intRow+intCount][intCol]);
					return false;
				}
			}
		} else {
			for (int intCount=0; intCount<intSize; intCount++) {
				if (playPanel.intYourGrid[intRow][intCol+intCount] != 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void placeShip(int intRow, int intCol, int intSize, boolean blnHorizontal, int intShip) {
		// Updates the 2D array intYourGrid[][] to show that your ship has been placed
		// The values 1-5 represent ships 1-5
		if (blnHorizontal == false) {
			for (int intCount=0; intCount<intSize; intCount++) {
				playPanel.intYourGrid[intRow+intCount][intCol] = intShip;
			}
		} else {
			for (int intCount=0; intCount<intSize; intCount++) {
				playPanel.intYourGrid[intRow][intCol+intCount] = intShip;
			}
		}
	}
	
	public int calcRow(int intY) {
		// Calculate the Row (1 to 10) based on your mouse cursor position
		return (int)Math.floor(1.0 * (intY - 80) / 64) + 1;
	}
	
	public int calcCol(int intX) {
		// Calculate the Column (1 to 10) based on your mouse cursor position
		return (int)Math.floor(1.0 * (intX - 80) / 64) + 1;
	}
	
	public void printYourGrid() { // Prints our your grid (for debugging)
		for (int intRow=1; intRow<=10; intRow++) {
			for (int intCol=1; intCol<=10; intCol++) {
				System.out.print(playPanel.intYourGrid[intRow][intCol] + " ");
			}
			System.out.println();
		}
	}
	
	public void printOpponentGrid() { // Prints out opponent grid (for debugging)
		for (int intRow=1; intRow<=10; intRow++) {
			for (int intCol=1; intCol<=10; intCol++) {
				System.out.print(playPanel.intOpponentGrid[intRow][intCol] + " ");
			}
			System.out.println();
		}
	}

	
	//Constructor
	public BattleshipGame() {
		// Battleship Play Panel
		playPanel.setLayout(null);
		playPanel.setPreferredSize(new Dimension(1280, 720));
		playPanel.addMouseMotionListener(this);
		playPanel.addMouseListener(this);
		rotateButton.setFont(font1);
		rotateButton.setBounds(1000, 360, 240, 80);
		rotateButton.addActionListener(this);
		readyButton.setFont(font1);
		readyButton.setBounds(1000, 460, 240, 80);
		readyButton.addActionListener(this);
		yourMsgField.setFont(font1);
		yourMsgField.setEditable(false);
		yourMsgField.setBounds(1000, 560, 240, 60);
		opponentMsgField.setFont(font1);
		opponentMsgField.setEditable(false);
		opponentMsgField.setBounds(1000, 640, 240, 60);
		playPanel.add(rotateButton);
		playPanel.add(readyButton);
		playPanel.add(yourMsgField);
		playPanel.add(opponentMsgField);

		// Help Panel
		helpPanel.setLayout(null);
		helpPanel.setPreferredSize(new Dimension(1280, 720));
		nextButton1.setBounds(540, 650, 200, 50);
		nextButton1.setFont(font1);
		nextButton1.addActionListener(this);
		helpPanel.add(nextButton1);
		
		// Help Panel 2
		helpPanel2.setLayout(null);
		helpPanel2.setPreferredSize(new Dimension(1280, 720));
		helpPanel2.addMouseListener(this);
		helpPanel2.addMouseMotionListener(this);
		step1.setBounds(300, 470, 600, 50);
		step1.setFont(font1);
		step1.setHorizontalAlignment(JTextField.CENTER);
		step1.setEditable(false);
		step1rotate.setBounds(230, 530, 700, 50);
		step1rotate.setFont(font1);
		step1rotate.setHorizontalAlignment(JTextField.CENTER);
		step1rotate.setEditable(false);
		step1ready.setBounds(280, 590, 600, 50);
		step1ready.setFont(font1);
		step1ready.setHorizontalAlignment(JTextField.CENTER);
		step1ready.setEditable(false);
		step1rotateButton.setFont(font1);
		step1rotateButton.setBounds(1000, 360, 240, 80);
		step1rotateButton.addActionListener(this);
		step1readyButton.setFont(font1);
		step1readyButton.setBounds(1000, 460, 240, 80);
		step1readyButton.addActionListener(this);
		step1yourMsgField.setFont(font1);
		step1yourMsgField.setEditable(false);
		step1yourMsgField.setBounds(1000, 560, 240, 60);
		step1opponentMsgField.setFont(font1);
		step1opponentMsgField.setEditable(false);
		step1opponentMsgField.setBounds(1000, 640, 240, 60);
		nextButton2.setBounds(540, 650, 200, 50);
		nextButton2.setFont(font1);
		nextButton2.addActionListener(this);
		yourmsgfield.setBounds(400, 510, 500, 50);
		yourmsgfield.setFont(font1);
		yourmsgfield.setHorizontalAlignment(JTextField.CENTER);
		yourmsgfield.setVisible(false);
		yourmsgfield.setEditable(false);
		opponentmsgfield.setBounds(300, 590, 660, 50);
		opponentmsgfield.setFont(font1);
		opponentmsgfield.setHorizontalAlignment(JTextField.CENTER);
		opponentmsgfield.setVisible(false);
		opponentmsgfield.setEditable(false);
		bothready.setBounds(300, 590, 660, 50);
		bothready.setFont(font1);
		bothready.setHorizontalAlignment(JTextField.CENTER);
		bothready.setVisible(false);
		bothready.setEditable(false);
		nextButton3.setBounds(540, 660, 200, 50);
		nextButton3.setFont(font1);
		nextButton3.addActionListener(this);
		nextButton3.setVisible(false);
		nextButton4.setVisible(false);
		nextButton4.setBounds(540, 650, 200, 50);
		nextButton4.setFont(font1);
		nextButton4.addActionListener(this);
		step2.setVisible(false);
		step2.setBounds(10, 530, 1000, 50);
		step2.setFont(font1);
		step2.setHorizontalAlignment(JTextField.CENTER);
		step2.setEditable(false);
		step2info.setVisible(false);
		step2info.setBounds(450, 590, 300, 50);
		step2info.setFont(font1);
		step2info.setHorizontalAlignment(JTextField.CENTER);
		step2info.setEditable(false);
		nextButton5.setVisible(false);
		nextButton5.setBounds(540, 650, 200, 50);
		nextButton5.setFont(font1);
		nextButton5.addActionListener(this);
		step2hit.setVisible(false);
		step2hit.setBounds(180, 530, 800, 50);
		step2hit.setFont(font1);
		step2hit.setHorizontalAlignment(JTextField.CENTER);
		step2hit.setEditable(false);
		step2hitinfo.setVisible(false);
		step2hitinfo.setBounds(200, 590, 770, 50);
		step2hitinfo.setFont(font1);
		step2hitinfo.setHorizontalAlignment(JTextField.CENTER);
		step2hitinfo.setEditable(false);
		nextButton6.setVisible(false);
		nextButton6.setBounds(540, 650, 200, 50);
		nextButton6.setFont(font1);
		nextButton6.addActionListener(this);
		step2miss.setVisible(false);
		step2miss.setBounds(180, 530, 800, 50);
		step2miss.setFont(font1);
		step2miss.setHorizontalAlignment(JTextField.CENTER);
		step2miss.setEditable(false);
		step2missinfo.setVisible(false);
		step2missinfo.setBounds(200, 590, 770, 50);
		step2missinfo.setFont(font1);
		step2missinfo.setHorizontalAlignment(JTextField.CENTER);
		step2missinfo.setEditable(false);
		nextButton7.setVisible(false);
		nextButton7.setBounds(540, 650, 200, 50);
		nextButton7.setFont(font1);
		nextButton7.addActionListener(this);
		helpPanel2.add(step1);
		helpPanel2.add(step1rotate);
		helpPanel2.add(step1ready);
		helpPanel2.add(step1rotateButton);
		helpPanel2.add(step1readyButton);
		helpPanel2.add(step1yourMsgField);
		helpPanel2.add(step1opponentMsgField);
		helpPanel2.add(nextButton2);
		helpPanel2.add(yourmsgfield);
		helpPanel2.add(opponentmsgfield);
		helpPanel2.add(nextButton3);
		helpPanel2.add(bothready);
		helpPanel2.add(nextButton4);
		helpPanel2.add(step2);
		helpPanel2.add(step2info);
		helpPanel2.add(nextButton5);
		helpPanel2.add(step2hit);
		helpPanel2.add(step2hitinfo);
		helpPanel2.add(nextButton6);
		helpPanel2.add(step2miss);
		helpPanel2.add(step2missinfo);
		helpPanel2.add(nextButton7);
		
		// Home Panel
		homePanel.setLayout(null);
		homePanel.setPreferredSize(new Dimension(1280, 720));
		joinIP.setBounds(700, 300, 200, 50);
		joinIP.setFont(font1);
		joinButton.setBounds(700, 370, 200, 80);
		joinButton.setFont(font1);
		hostButton.setBounds(380, 330, 200, 80);
		hostButton.setFont(font1);
		helpButton.setBounds(540, 500, 200, 80);
		helpButton.setFont(font1);
		quitButton.setBounds(540, 600, 200, 80);
		quitButton.setFont(font1);
		themesButton.setBounds(1000, 600, 200, 80);
		themesButton.setFont(font1);
		homePanel.add(joinIP);
		homePanel.add(joinButton);
		homePanel.add(hostButton);
		homePanel.add(helpButton);
		homePanel.add(quitButton);
		homePanel.add(themesButton);
		hostButton.addActionListener(this);
		joinButton.addActionListener(this);
		helpButton.addActionListener(this);
		quitButton.addActionListener(this);
		themesButton.addActionListener(this);
		
		
		//Themes Panel
		themesPanel.setLayout(null);
		themesPanel.setPreferredSize(new Dimension(1280, 720));
		themesPanel.add(homeButton);
		homeButton.setBounds(1000, 600, 200, 80);
		homeButton.setFont(font1);
		homeButton.addActionListener(this);
		
		themes1Button.setSize(300, 50);
		themes1Button.setLocation(500, 300);
		themesPanel.add(themes1Button);
		
		themes2Button.setSize(300, 50);
		themes2Button.setLocation(500, 350);
		themesPanel.add(themes2Button);
		
		themes3Button.setSize(300, 50);
		themes3Button.setLocation(500, 400);
		themesPanel.add(themes3Button);
		
		themes4Button.setSize(300, 50);
		themes4Button.setLocation(500, 450);
		themesPanel.add(themes4Button);
		
		buttonGroup = new ButtonGroup();
		buttonGroup.add(themes1Button);
		buttonGroup.add(themes2Button);
		buttonGroup.add(themes3Button);
		buttonGroup.add(themes4Button);
		
		confirmButton.setSize(300, 50);
		confirmButton.setLocation(500, 500);
		themesPanel.add(confirmButton);
		confirmButton.addActionListener(this);
		
		themesLabel = new JLabel("", SwingConstants.CENTER);
		themesLabel.setSize(400,100);
		themesLabel.setLocation(800,300);
		themesPanel.add(themesLabel);
		
		// Frame
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theFrame.setContentPane(homePanel);
		theFrame.pack();
		theFrame.setVisible(true);	
		theFrame.setResizable(false);
		
		// Timer
		theTimer.start();
		animTimer.start();
	}

	//Main Method
	public static void main(String[] args) {
		new BattleshipGame();
	}
}
