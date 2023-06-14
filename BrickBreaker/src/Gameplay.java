import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.Timer;

public class Gameplay extends JPanel implements KeyListener, ActionListener
{
	private boolean play = false;
	private int score = 0;
	private int highScore = 0;

	private int totalBricks = 48;

	private Timer timer;
	private int delay=8;

	private int playerX = 310;

	private int ballposX = 120;
	private int ballposY = 350;
	private int ballXdir = -4; // speed of ball in X direction
	private int ballYdir = -7; // speed of ball in Y direction

	private MapGenerator map;

	public Gameplay()
	{
		map = new MapGenerator(4, 12);
		Datastore.loadHighScore();
		highScore = Datastore.getHighScore();
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
        timer=new Timer(delay,this);
		timer.start();
	}

	@Override
	public void paint(Graphics g)
	{
		// background
		g.setColor(Color.pink);
		g.fillRect(1, 1, 692, 592);

		// drawing map
		map.draw((Graphics2D) g);

		// borders
		g.setColor(Color.magenta);
		g.fillRect(0, 0, 3, 592);
		g.fillRect(0, 0, 692, 3);
		g.fillRect(691, 0, 3, 592);

		// the scores
		g.setColor(Color.black);
		g.setFont(new Font("serif",Font.BOLD, 25));
		g.drawString(""+score, 590,30);

		// high score text
		g.setColor(Color.yellow);
		g.setFont(new Font("serif", Font.BOLD, 25));
		g.drawString("High Score: "+highScore, 300, 30);

		// the paddle
		g.setColor(Color.magenta);
		g.fillRect(playerX, 550, 100, 8);

		// the ball
		g.setColor(Color.white);
		g.fillOval(ballposX, ballposY, 20, 20);

		// when you won the game
		if(totalBricks <= 0)
		{
			 play = false;
             ballXdir = 0;
     		 ballYdir = 0;

			  if (score > highScore) {
				  highScore = score;
				  Datastore.saveHighScore(score);

				  g.setColor(Color.YELLOW);
				  g.setFont(new Font("serif",Font.BOLD, 20));
				  g.drawString("NEW HIGH SCORE!!!", 200,100);
			  }

             g.setColor(Color.white);
             g.setFont(new Font("serif",Font.BOLD, 30));
             g.drawString("You Won!!", 260,300);

             g.setColor(Color.white);
             g.setFont(new Font("serif",Font.BOLD, 20));
             g.drawString("Press the [ENTER] button to Restart", 200,350);
		}

		// when you lose the game
		if(ballposY > 570)
        {
			if (score > highScore) {
				highScore = score;
				Datastore.saveHighScore(score);

				g.setColor(Color.YELLOW);
				g.setFont(new Font("serif",Font.BOLD, 30));
				g.drawString("NEW HIGH SCORE!!!", 200,280);
			}
			 play = false;
             ballXdir = 0;
     		 ballYdir = 0;
             g.setColor(Color.white);
             g.setFont(new Font("serif",Font.BOLD, 30));
             g.drawString("Game Over :( ", 250,320);

			g.setColor(Color.white);
			g.setFont(new Font("serif",Font.BOLD, 15));
			g.drawString("SCORE: "+score, 310,340);

             g.setColor(Color.white);
             g.setFont(new Font("serif",Font.BOLD, 20));
             g.drawString("Press the (Enter) button to Restart", 200,360);
        }

		g.dispose();
	}

	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_E) {
			highScore = 0;
			Datastore.resetHighScore();
		}

		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			if(playerX >= 600)
			{
				playerX = 600;
			}
			else
			{
				moveRight();
			}
        }

		if (e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			if(playerX < 10)
			{
				playerX = 10;
			}
			else
			{
				moveLeft();
			}
        }
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			if(!play) // ! == NOT, play == true, !play == false 
			{
				play = true;
				ballposX = 120;
				ballposY = 350;
				ballXdir = -6;
				ballYdir = -10;
				playerX = 310;
				score = 0;
				totalBricks = 48;
				map = new MapGenerator(4, 12);

				repaint();
			}
        }
	}

	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	public void moveRight()
	{
		play = true;
		playerX+=45;
	}

	public void moveLeft()
	{
		play = true;
		playerX-=45;
	}

	public void actionPerformed(ActionEvent e)
	{
		timer.start();
		if(play)
		{
			if(new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 550, 30, 8)))
			{
				ballYdir = -ballYdir;
				ballXdir = -4;
			}
			else if(new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX + 70, 550, 30, 8)))
			{
				ballYdir = -ballYdir;
				ballXdir = ballXdir + 4;
			}
			else if(new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX + 30, 550, 40, 8)))
			{
				ballYdir = -ballYdir;
			}

			// check map collision with the ball
			A: for(int i = 0; i<map.map.length; i++)
			{
				for(int j =0; j<map.map[0].length; j++)
				{
					if(map.map[i][j] > 0)
					{
						//scores++;
						int brickX = j * map.brickWidth + 80;
						int brickY = i * map.brickHeight + 50;
						int brickWidth = map.brickWidth;
						int brickHeight = map.brickHeight;

						Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
						Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);
						Rectangle brickRect = rect;

						if(ballRect.intersects(brickRect))
						{
							map.setBrickValue(0, i, j);
							score+=5;
							totalBricks--;

							// when ball hit right or left of brick
							if(ballposX + 19 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width)
							{
								ballXdir = -ballXdir;
							}
							// when ball hits top or bottom of brick
							else
							{
								ballYdir = -ballYdir;
							}

							break A;
						}
					}
				}
			}

			ballposX += ballXdir;
			ballposY += ballYdir;

			if(ballposX < 0)
			{
				ballXdir = -ballXdir;
			}
			if(ballposY < 0)
			{
				ballYdir = -ballYdir;
			}
			if(ballposX > 670)
			{
				ballXdir = -ballXdir;
			}

			repaint();
		}
	}
}
