/*
 * Copyright © 2014 Marc Liberatore.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the University of Massachusetts.
 */

/**
 * Created by liberato on 10/8/14.
 */

public class Cell {
    public final int x;
    public final int y;
    private SumAndUniqueConstraint[] sumGroups = {null, null};
    private boolean[] posInts = {true, true, true, true, true, true, true, true, true};

    public Cell (int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setConstraintGroup(SumAndUniqueConstraint grp) {
    	if (sumGroups[0] == null) {
    		sumGroups[0] = grp;
    	}
    	else if (sumGroups[1] == null) {
    		sumGroups[1] = grp;
    		setPosInts(sumGroups[0], sumGroups[1]);
    	}
    	else {
    		System.out.println("Constraint Group assignment error: cell (" + this.x + "," + this.y + ")\n");
    	}
    }
    
    public SumAndUniqueConstraint[] getSumGroups() {
    	return sumGroups;
    }
    
    public void setSumGroup(SumAndUniqueConstraint sauc, int idx) {
    	sumGroups[idx] = sauc;
    }
    
    public void setPosInts(SumAndUniqueConstraint sauc1, SumAndUniqueConstraint sauc2) {
    	boolean[] temp1 = {false, false, false, false, false, false, false, false, false};
    	for (SumSet s : sauc1.getSets()) {
    		for (Integer i=1; i<10; i++) {
    			if (s.contains(i)) {
    				temp1[i-1] = true;
    			}
    		}
    	}
    	boolean[] temp2 = {false, false, false, false, false, false, false, false, false};
    	for (SumSet s : sauc2.getSets()) {
    		for (Integer i=1; i<10; i++) {
    			if (s.contains(i)) {
    				temp2[i-1] = true;
    			}
    		}
    	}
    	for (int i=0; i<9; i++) {
    		if (!(temp1[i] == true && temp2[i] == true)) {
    			posInts[i] = false;
    		}
    	}
    }
    
    public void setPosInts(boolean[] newPos) {
    	if (newPos.length != 9) {
    		System.out.println("Error in setPosInts: input boolean[] param has length other than 9");
    	}
    	
    	posInts = newPos;
    }
    
    public boolean[] getPosInts() {
    	return posInts;
    }
    
    public void intNotPossible(int i) {
    	posInts[i-1] = false;
    }
    
    public int getNumPos() {
    	int possibles = 0;
    	for (int i=0; i<9; i++) {
    		if (posInts[i] == true) {
    			possibles++;
    		}
    	}
    	return possibles;
    }
    
    @Override
    public Cell clone() {
    	Cell result = new Cell(x, y);
    	boolean[] newPos = new boolean[9];
    	for (int i=0; i<9; i++) {
    		newPos[i] = posInts[i];
    	}
    	result.setPosInts(newPos);
    	result.setConstraintGroup(sumGroups[0]);
    	result.setConstraintGroup(sumGroups[1]);
    	return result;
    }

    @Override
    public String toString() {
        return x + " " + y;
    }


    /*
     * Many of Java's conveniences (e.g., Sets, Maps, and their implementing HashSet and HashMap classes)
     *  won't work if you don't implement a meaningful equals() and hashCode() method. IDEs (like IntelliJ)
     * can do this for you.
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;

        if (x != cell.x) return false;
        if (y != cell.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

}
