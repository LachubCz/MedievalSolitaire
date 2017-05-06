package ija.ija2016.homework3.view;
import ija.ija2016.homework3.controller.CommandInterface;
import java.util.ArrayList;
import javax.swing.JPanel;
import ija.ija2016.homework3.model.cards.RepaintInterface;
import ija.ija2016.homework3.controller.CommandBuilder;
import ija.ija2016.homework3.controller.CommandControl;
import ija.ija2016.homework3.model.cards.Card;
import ija.ija2016.homework3.model.cards.CardBoard;
import ija.ija2016.homework3.model.cards.CardBoardInterface;
import ija.ija2016.homework3.model.cards.CardDeck;
import ija.ija2016.homework3.model.cards.CardDeckInterface;
import ija.ija2016.homework3.model.cards.CardHint;
import ija.ija2016.homework3.model.cards.CardInterface;
import ija.ija2016.homework3.model.cards.CardStack;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;



public class BoardView extends JPanel implements RepaintInterface {
	private CardDeck selectedSource = null;
	private Card selectedMultiMoveCard = null;

    private CardView selectedSourceCard = null;

	private static final long serialVersionUID = 1L;

	private CommandBuilder commander;
	private CardBoard cardBoard;
	private CardPackView mainCardPicker; //standard deck

	private ArrayList<CardDeckView> decks = new ArrayList<>(); //target pack
	private ArrayList<CardStackView> stacks = new ArrayList<>(); //working pack

    private boolean hintNeeded = false;

	public BoardView(CardBoard newCardBoard) {
		commander = new CommandBuilder(newCardBoard);
		this.setLayout(null);
		cardBoard = newCardBoard;
		newCardBoard.registerObserver((RepaintInterface)this);
		this.CreateAll();
	}

	private void CreateAll() {
                JButton buttonSave = new JButton("Save");
                buttonSave.setBounds(0, 0, 80, 25);
                this.add(buttonSave);
                
                JButton buttonHint = new JButton("Hint Off");
                buttonHint.setBounds(80, 0, 80, 25);
                this.add(buttonHint);
                
		JButton buttonUndo = new JButton("Undo");
                buttonUndo.setBounds(160, 0, 80, 25);
                this.add(buttonUndo);

                JButton buttonLoad = new JButton("Load");
                buttonLoad.setBounds(320, 0, 80, 25);
                this.add(buttonLoad);
                
                JButton buttonClose = new JButton("Close");
                buttonClose.setBounds(400, 0, 80, 25);
                this.add(buttonClose);
                
                int basicValue = this.getHeight();
                int cardSpace = (int)(basicValue / 4.4);
                
                CardPackView packPicker = new CardPackView();
                packPicker.setModel(cardBoard.getSourcePack());
                packPicker.setXY(cardSpace * (1), 35);
                packPicker.setPanel(this);
                packPicker.paint();
                
                mainCardPicker = packPicker;
                
                for(int i = 0; i < 7; i++) {
                    CardStackView stack = new CardStackView();
                    stack.setModel(cardBoard.getStack(i));
                    stack.setXY(cardSpace * (i+1), (int)(basicValue / 2.4 )  );
                    stack.setPanel(this);
                    stack.paint();
                    stacks.add(stack);
		}
                
                for(int i = 0; i < 4; i++) {
                    CardDeckView deck = new CardDeckView();
                    deck.setModel(cardBoard.getDeck(i));
                    deck.setXY(cardSpace * (i+4), 35  );
                    deck.setPanel(this);
                    deck.paint();
                    decks.add(deck);
		}

                buttonSave.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        String fileName = JOptionPane.showInputDialog(null, "Enter file name:", "Dialog for file name", JOptionPane.WARNING_MESSAGE);
		        if(fileName.length() > 0){
		        	CommandInterface command = new CommandControl("save", new ArrayList<String>(){{add("saves/" + fileName);}});
		        	commander.execute(command);
		        }
		    }
		});

                

                buttonHint.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	if(hintNeeded) {
                            hintNeeded = false;
                            clearHints();
                            buttonHint.setText("Hint Off");
                        }
                        else {
                            hintNeeded = true;
                            buttonHint.setText("Hint On");
                            setSelectedSource(selectedSource, selectedSourceCard);
                        }
		    }
		});
                
		buttonUndo.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	CommandInterface command = new CommandControl("undo");
		    	commander.execute(command);
		    }
		});
                
                buttonLoad.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        loadFile();
		    }
		});

                buttonClose.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
		    	closeThisBoard();
		    }
		});
	}
        
        public void Recreate() {
            removeComponents();
            this.removeAll();
            
            if(this.cardBoard != null) {
                if(this.cardBoard.isGameOver()) {
                     CreateGameOver();
                }
                else {
                    this.CreateAll();
                }
            }
            super.repaint();
            this.revalidate();
        }
        
        public void CreateGameOver(){
            JButton buttonClose= new JButton("Game Over");
            buttonClose.setBounds(250, 60, 140, 80);
            this.add(buttonClose); 
            
		buttonClose.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	closeThisBoard();
		    }
		});
	}
        
        public void loadFile() {
        }
        
        public void closeThisBoard() {
            MainView mainWindow = (MainView)this.getTopLevelAncestor();
            mainWindow.removeBoard(this);
        }
        
        private void removeComponents() {
            Component [] components = this.getComponents();
            for(Component component: components) {
                MouseListener[] listeners = component.getMouseListeners();
                for(MouseListener listener: listeners) {
                    this.removeMouseListener(listener);
                }
            }
        }

        public CommandBuilder getCommandBuilder() {
            return commander;
        }
        
        public CardDeck getSelectedSource() {
            return this.selectedSource;
        }
        
        public void setSelectedSource(CardDeck deck) {
            this.selectedSource = deck;
        }
        
                public void setSelectedSource(CardDeck deck, CardView sourceCard) {
            if(this.selectedSourceCard != null) {
                this.selectedSourceCard.setSelected(false);
            }
            this.selectedSource = deck;
            this.selectedSourceCard = sourceCard;
            this.setMultiMoveCard(null);
            
		if(sourceCard != null){
		sourceCard.setSelected(true);
                    if(this.hintNeeded) {
                        this.createHints();
                    }
		}
        }
        
        public void unselectSelectedSource(){
            this.setSelectedSource(null, null);
	}
        
        public boolean isSourceSelected() {
            return this.selectedSource != null;
        }
        
        public Card getMultiMoveCard(){
            return this.selectedMultiMoveCard;
	}
        
	public void setMultiMoveCard(Card card) {
            this.selectedMultiMoveCard = card;
	}
        
        
    public void createHints() {
        int hint = this.cardBoard.createHint(this.selectedSourceCard.toCard());
        
        if(hint == -1)
        {
        	//nenasla se shoda
        }
        
        if(hint < 10)
        {
        	//hint = cislo targetpacku 
        }
        else
        {
        	//hint = cislo workingpacku
        	hint = hint - 10;
        }
	}
}
