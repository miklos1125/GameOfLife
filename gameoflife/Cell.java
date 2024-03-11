package gameoflife;

public class Cell{
    static int cellCount = 0;
    private boolean checked = false;
    private boolean alive = false;
    private boolean nextChange = false;
    private int x, y;

        
    Cell(int tempX, int tempY){
        this.x = tempX * GameOfLife.cellSize;
        this.y = tempY * GameOfLife.cellSize;
    }
        
    public void changer(){
        alive = !alive;
        if (alive){
            cellCount ++;
        } else {
            cellCount--;
        }
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y; 
    }
    
    public boolean isAlive(){
        return alive;
    }
    
    public boolean isChecked(){
        return checked;
    }
    
    public void setChecked(boolean tempL){
        checked = tempL;
    }

    
    public boolean isNextChange(){
        return nextChange;
    }
    
    public void setNextChange(boolean tempNext){
        this.nextChange = tempNext;
    }
    
    public void setDead(){
        if (alive){
            alive = false;
            cellCount--;
        }
    }
    
    public void setAlive(){
        if (!alive){
            alive = true;
            cellCount++;  
        }
    }
}   