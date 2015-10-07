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

import java.util.*;

/**
 * Created by liberato on 10/8/14.
 */
public class SumAndUniqueConstraint {
    public final Integer sum;
    public final Integer len;
    public final List<Cell> cells;
    private List<SumSet> sets;

    public SumAndUniqueConstraint(Integer sum, List<Cell> cells, boolean initSets) {
        this.sum = sum;
        this.len = cells.size();
        this.cells = cells;
        if (initSets) {
        	initSets();
        }
    }
    
    public void initSets() {
    	sets = SumSet.readSets(sum, len);
    }
    
    public List<SumSet> getSets() {
    	return sets;
    }
    
    public void setSets(List<SumSet> sets) {
    	this.sets = sets;
    }
    
    public SumAndUniqueConstraint checkSets(Cell cell, int val, Map<Cell, Integer> assigns) {
    	SumAndUniqueConstraint result;
    	
    	List<Cell> newCells = new ArrayList<Cell>(cells);
    	
    	boolean elimSets = cells.contains(cell);
    	List<SumSet> newSets = new ArrayList<SumSet>(sets);
    	if (elimSets) {
    		for (SumSet s : sets) {
    			if (!s.contains((Integer)val)) {
    				newSets.remove(s);
    			}
    		}
    	}
    	
    	result = new SumAndUniqueConstraint(sum, newCells, false);
    	result.setSets(newSets);
    	if (elimSets) {
    		for (Cell c : assigns.keySet()) {
    			if (result.cells.contains(c)) {
    				int idx = 0;
    				for (int i=0; i<2; i++) {
    					if (c.getSumGroups()[i].equals(this)) {
    						idx = (i+1) % 2;
    						c.setSumGroup(this, i);
    						break;
    					}
    				}
    				c.setPosInts(result, c.getSumGroups()[idx]);
    				c.intNotPossible(val);
    			}
    		}
    	}
    	return result;
    }
    
    @Override
    public boolean equals(Object o) {
    	SumAndUniqueConstraint other = (SumAndUniqueConstraint)o;
    	return cells.equals(other.cells);
    }
}
