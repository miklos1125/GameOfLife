package gameoflife;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.URL;
import javax.swing.*;

class GameOfLife extends JFrame {

    public static void main(String[] args) {
        GameOfLife gof = new GameOfLife();  
    }
    
    static int cellSize = 20;
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    int high = d.height / 10 * 9;
    int yCellsMax = (high - 180) / cellSize;
    int xCellsMax = 1000/cellSize;
    
    URL u = this.getClass().getResource("pic/gof.png");
    Image icon = Toolkit.getDefaultToolkit().createImage(u);
    URL u2 = this.getClass().getResource("pic/bck.png");
    Image backIm = Toolkit.getDefaultToolkit().createImage(u2);

    Fields field;
    Cell[][] cells = new Cell[xCellsMax][yCellsMax];
    boolean isRunning = false;
    
    byte [][] direction = {{-1,-1}, {-1,0}, {-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}}; 

    Life life;
    int speed = 600;
    int generations = 1;
    int islands;
    
    Color darkGreen = new Color(0, 100, 0);
    Color lightYellow = new Color(255, 250, 225);
    Color redish = new Color(255, 100, 0);
    Color paleYellow = new Color(255, 255, 200);
    
    Font bigFont = new Font("Times New Roman", 20, 40);
    Font smallFont = new Font("Times New Roman", 20, 30);

    JButton start, stop, oneStep;
    MenuItem sta, sto, clear, reset, random;
    OnOff onoff;
    Refield refield;
    Scrollbar tempo, zoom;
    
    Label info;
    JTextField counter, gener, island;

    //Main Window:
    GameOfLife() {
        super("Game of Life");
        setSize(1100, high + 25);
        setLocation(d.width / 2 - 505, (d.height / 2 - high / 2) / 2);
        setResizable(false);
        setLayout(null);
        setVisible(true);
        addWindowListener(new Close());
        setIconImage(icon);

        MenuBar mb = new MenuBar();
        setMenuBar(mb);

        Menu file = new Menu("File");
        mb.add(file);
        MenuItem load = new MenuItem("Load");
        load.setEnabled(false);
        MenuItem save = new MenuItem("Save as");
        save.setEnabled(false);
        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(new ActionListener(){
           @Override
           public void actionPerformed(ActionEvent ae){
               System.exit(0);
           }
        });
        file.add(load);
        file.add(save);
        file.add(exit);
        
        onoff = new OnOff();
        refield = new Refield();

        Menu game = new Menu("Game");
        mb.add(game);
        random = new MenuItem("Randomize");
        random.addActionListener(refield);
        clear = new MenuItem("Clear cells");
        clear.addActionListener(refield);
        sta = new MenuItem("Start");
        sta.addActionListener(onoff);
        sto = new MenuItem("Stop");
        sto.addActionListener(onoff);
        reset = new MenuItem("Reset all");
        reset.addActionListener(refield);
        game.add(sta);
        game.add(sto);
        game.add(random);
        game.add(clear);
        game.add(reset);

        Menu about = new Menu("About");
        mb.add(about);
        MenuItem agof = new MenuItem("Game of Life");
        MenuItem rules = new MenuItem("Rules");
        about.add(agof);
        agof.addActionListener(new ActionListener(){
           @Override
           public void actionPerformed(ActionEvent ae){
                JOptionPane.showMessageDialog(null, 
                "Conway's Game of Life\n\n"
                + "The Game of Life, also known simply as Life,\n"
                + "is a cellular automaton devised by the British\n"
                + "mathematician John Horton Conway in 1970.\n\n"
                + "It is a zero-player game, meaning that its\n"
                + "evolution is determined by its initial state,\n"
                + "requiring no further input.\n\n"
                + "One interacts with the Game of Life by creating\n"
                + "an initial configuration and observing how it evolves.", "About GAME of LIFE",1);
           }
        });
        about.add(rules);
        rules.addActionListener(new ActionListener(){
           @Override
           public void actionPerformed(ActionEvent ae){
               JOptionPane.showMessageDialog(null,
               "1. Any live cell with fewer than two live neighbors dies, "
                       + "as if by underpopulation.\n"
               +"2. Any live cell with two or three live neighbors lives on "
                       + "to the next generation.\n"
               +"3. Any live cell with more than three live neighbors dies, "
                       + "as if by overpopulation.\n"
               +"4. Any dead cell with exactly three live neighbors becomes "
                       + "a live cell, as if by reproduction.", 
                "Operating Rules",1);
            }
        });

        Background b = new Background();
        add(b); 
    }

    class Background extends JPanel {
        //Background Image:
        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(backIm, 0, 0, this);
            //Black inner frame
            g.setColor(Color.black);
            g.fillRect(45, 35, 1011, yCellsMax * cellSize + 11);
        }

        Background() {
            setSize(1100, high);
            setLayout(null);
            
            //   Setting game field/cells:     
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    cells[i][j] = new Cell(i, j);
                }
            }
            field = new Fields();
            add(field);
            
            // Buttons, etc.:     
            start = new JButton("Start");
            start.setBounds(40, high - 120, 100, 40);
            start.setBackground(redish);
            start.addActionListener(onoff);
            add(start);

            stop = new JButton("Stop");
            stop.setBounds(160, high - 120, 100, 40);
            stop.setBackground(Color.orange);
            stop.addActionListener(onoff);
            add(stop);

            oneStep = new JButton("One step");
            oneStep.setBounds(280, high - 120, 100, 40);
            oneStep.setBackground(paleYellow);
            oneStep.addActionListener(onoff);
            add(oneStep);

            info = new Label(" You can add or remove cells, with a mouse-click.");
            info.setBounds(70, high - 65, 275, 20);
            info.setBackground(lightYellow);
            info.setForeground(darkGreen);
            add(info);

            Label cellText = new Label("   Cell counter");
            cellText.setBounds(405, high - 120, 90, 20);
            cellText.setBackground(paleYellow);
            cellText.setForeground(darkGreen);
            add(cellText);

            counter = new JTextField(String.valueOf(Cell.cellCount));
            counter.setBounds(405, high - 100, 90, 50);
            counter.setFont(bigFont);
            counter.setEditable(false);
            counter.setHorizontalAlignment(JTextField.CENTER);
            counter.setForeground(darkGreen);
            counter.setBackground(lightYellow);
            add(counter);
            
            Label genText = new Label("   Generations");
            genText.setBounds(515, high - 120, 90, 20);
            genText.setBackground(paleYellow);
            genText.setForeground(redish);
            add(genText);
            
            gener = new JTextField(String.valueOf(generations));
            gener.setBounds(515, high - 100, 90, 50);
            gener.setFont(bigFont);
            gener.setEditable(false);
            gener.setHorizontalAlignment(JTextField.CENTER);
            gener.setForeground(redish);
            gener.setBackground(lightYellow);
            add(gener);
            
            Label islandText = new Label("       Islands");
            islandText.setBounds(625, high - 120, 90, 20);
            islandText.setBackground(paleYellow);
            islandText.setForeground(Color.BLACK);
            add(islandText);
            
            island = new JTextField(String.valueOf(islands));
            island.setBounds(625, high - 100, 90, 50);
            island.setFont(bigFont);
            island.setEditable(false);
            island.setHorizontalAlignment(JTextField.CENTER);
            island.setForeground(Color.ORANGE);
            island.setBackground(lightYellow);
            add(island);

            Label push = new Label("Push the tempo!");
            push.setBounds(740, high - 100, 95, 20);
            push.setBackground(lightYellow);
            push.setForeground(darkGreen);
            add(push);

            tempo = new Scrollbar(Scrollbar.HORIZONTAL, speed, 100, 0, 1520);
            tempo.setBounds(850, high - 100, 200, 20);
            tempo.setName("Push the tempo!");
            tempo.setBackground(redish);
            tempo.addAdjustmentListener(new AdjustmentListener(){
                @Override
                public void adjustmentValueChanged(AdjustmentEvent ae) {
                    speed = tempo.getValue();

                }
            });
            add(tempo);

            /*Label zm = new Label("Zoom in/out");
            zm.setBounds(765, high - 75, 70, 20);
            zm.setBackground(lightYellow);
            zm.setForeground(darkGreen);
            add(zm); 

            zoom = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, -10, 10);
            zoom.setBounds(850, high - 75, 200, 20);
            zoom.setName("Zoom in/out");
            zoom.setBackground(Color.orange);
            add(zoom);*/
        }
    }
    
    //"Playground graphix" class
    class Fields extends JPanel {
        Image img;
        BufferedImage bImage;
        Graphics graphics;

        Fields() {

            setBounds(50, 40, 1000, yCellsMax * cellSize);
            //setLayout(null);
            addMouseListener(new Mouse());
 
        }
        //Draw the cells:
        @Override
        public void paint(Graphics g) {
            img = createImage();
            g.drawImage(img, 0, 0, this);
            try {
                if (Cell.cellCount >=9999){
                    counter.setFont(smallFont);
                } else {
                    counter.setFont(bigFont);
                }
                counter.setText(String.valueOf(Cell.cellCount));
                countIslands();
                island.setText(String.valueOf(islands));
            } catch (Exception e) {
                System.out.println("This problem again! Why?");
            }
        }
        
        public Image createImage(){  
            bImage = new BufferedImage(1000, yCellsMax * cellSize, BufferedImage.SCALE_SMOOTH);
            graphics = bImage.getGraphics();
            graphics.setColor(lightYellow);
            graphics.fillRect(0, 0, 1000, yCellsMax * cellSize);
            graphics.setColor(darkGreen);
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    if (cells[i][j].isAlive()) {
                        graphics.fillRect(cells[i][j].getX(), cells[i][j].getY(), cellSize, cellSize);
                    } else {
                        graphics.drawRect(cells[i][j].getX(), cells[i][j].getY(), cellSize, cellSize);
                    }
                }
            }
            return bImage;
        }
    }

    boolean isOnMap(int x, int y){
        boolean goodPlace = x>=0 && x<cells.length && y>=0 && y<cells[0].length;
        return goodPlace;
    }
    
    void countIslands(){
        islands = 0;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {    
                if (cells[i][j].isAlive() && !cells[i][j].isChecked()){
                    islands++;
                    landFinder(i,j);   
                }
            }
        }
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j].setChecked(false);
            }
        }
    }
    
    void landFinder(int xx, int yy){
        cells[xx][yy].setChecked(true);
        for(int f= 0; f<direction.length; f++){
            if(isOnMap(xx+direction[f][0], yy+direction[f][1]) 
            && cells[xx+direction[f][0]][yy+direction[f][1]].isAlive()
            && !cells[xx+direction[f][0]][yy+direction[f][1]].isChecked()){
                landFinder(xx+direction[f][0], yy+direction[f][1]);
            }
        }
    }

    //THE BIG ON-OFF BUTTONS
    class OnOff implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if ((ae.getSource() == start || ae.getSource() == sta) && !isRunning) {
                isRunning = true;
                life = new Life();
                life.start();
            } else if ((ae.getSource() == stop || ae.getSource() == sto) && isRunning) {
                isRunning = false;
            } else if (ae.getSource() == oneStep) {
                if (!isRunning) {
                    life = new Life();
                    life.start();
                }
            }
        }
    }
    
    class Refield implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if(ae.getSource() == clear){
                for (int i = 0; i  < cells.length; i++){
                    for (int j = 0; j < cells[i].length; j++){
                        cells[i][j].setDead();
                    }
                }
                field.repaint();
            } else if (ae.getSource() == reset){
                isRunning = false;
                for (int i = 0; i  < cells.length; i++){
                    for (int j = 0; j < cells[i].length; j++){
                        cells[i][j].setDead();
                    }
                }
                gener.setText(String.valueOf(generations = 1));
                field.repaint();
                tempo.setValue(400);
                //zoom.setValue(0);
            } else if (ae.getSource() == random){
                for (int i = 0; i  < cells.length; i++){
                    for (int j = 0; j < cells[i].length; j++){
                        int rand = (int)(Math.random()*2);
                        if (rand == 0){ 
                            cells[i][j].setDead();
                        } else {
                            cells[i][j].setAlive();
                        }
                    }
                }
                field.repaint();
            }
        }
    }

    class Mouse implements MouseListener {
        //Changing cells
        @Override
        public void mouseReleased(MouseEvent me) {
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    if (me.getX() > cells[i][j].getX() && me.getX() < cells[i][j].getX() + cellSize
                            && me.getY() > cells[i][j].getY() && me.getY() < cells[i][j].getY() + cellSize) {
                        cells[i][j].changer();
                        field.repaint();
                    }
                }
            }
        }
        @Override
        public void mouseEntered(MouseEvent me) {
        } //Not supported
        @Override
        public void mouseExited(MouseEvent me) {
        } //Not supported
        @Override
        public void mousePressed(MouseEvent me) {
        } //Not supported
        @Override
        public void mouseClicked(MouseEvent me) {
        } //Not supported
    }

    class Close extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent we) {
            System.exit(0);
        }
    }

    class Life extends Thread{
        @Override
        public void run() {
            do {
                    //Search:
                    for (int i = 0; i < cells.length; i++) {
                        for (int j = 0; j < cells[i].length; j++) {
                            //Neighbours:
                            int neighbours = 0;
                            for (int k = i - 1; k <= i + 1; k++) {
                                for (int l = j - 1; l <= j + 1; l++) {
                                    if (!(k == i && l == j) && k >= 0 && k < cells.length
                                            && l >= 0 && l < cells[i].length && cells[k][l].isAlive()) {
                                        neighbours++;
                                    }
                                }
                            }
                            //Game of Life rules:
                            if (cells[i][j].isAlive()) {
                                if (neighbours < 2 || neighbours > 3) {
                                    cells[i][j].setNextChange(true);
                                }
                            } else {
                                if (neighbours == 3 ) {
                                    cells[i][j].setNextChange(true);
                                }
                            }

                        }
                    }
                    //Change:
                    for (int i = 0; i < cells.length; i++) {
                        for (int j = 0; j < cells[i].length; j++) {
                            if (cells[i][j].isNextChange()) {
                                cells[i][j].changer();
                                cells[i][j].setNextChange(false);
                            }
                        }
                    }
                    field.repaint();
                    gener.setText(String.valueOf(++generations));
                try {
                    sleep(1500-speed);
                } catch (InterruptedException ex) {
                    System.out.println("Problem!");
                }
            } while (isRunning);
        }
    }
}



