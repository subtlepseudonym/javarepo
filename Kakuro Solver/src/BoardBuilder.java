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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Created by liberato on 10/8/14.
 */
public class BoardBuilder {
    private final int width;
    private final int height;
    private final Set<Cell> filledCells;
    private final Map<Cell, Integer> horizontalClues;
    private final Map<Cell, Integer> verticalClues;

    private BoardBuilder(int width, int height, Set<Cell> filledCells, Map<Cell, Integer> horizontalClues, Map<Cell, Integer> verticalClues) {
        this.width = width;
        this.height = height;
        this.filledCells = filledCells;
        this.horizontalClues = horizontalClues;
        this.verticalClues = verticalClues;
    }

    /**
     *
     * @param boardString a board as described in the assignment
     * @return the corresponding Board
     * @throws IOException
     */
    public static Board fromString(String boardString) throws IOException {
        BufferedReader br = new BufferedReader(new StringReader(boardString));

        String line = br.readLine();
        String[] xAndY = line.split("\\s+");
        final int width = Integer.parseInt(xAndY[0]);
        final int height = Integer.parseInt(xAndY[1]);

        Set<Cell> filledCells = new HashSet<Cell>();
        Map<Cell, Integer> horizontalClues = new HashMap<Cell, Integer>();
        Map<Cell, Integer> verticalClues = new HashMap<Cell, Integer>();

        while ((line = br.readLine()) != null) {
            Scanner s = new Scanner(line);
            int x = s.nextInt();
            int y = s.nextInt();
            String type = s.next();
            int value = s.nextInt();

            Cell cell = new Cell(x,y);

            if (type.equals("x")) {
                filledCells.add(cell);
            }
            else if (type.equals("h")) {
                horizontalClues.put(cell, value);
            }
            else if (type.equals("v")) {
                verticalClues.put(cell, value);
            }
            else {
            	s.close();
                throw new RuntimeException("unhandled type : " + type);
            }
            s.close();
        }

        BoardBuilder bb = new BoardBuilder(width, height, filledCells, horizontalClues, verticalClues);

        return bb.build();
    }

    private boolean isOpenCell(Cell cell) {
        if ((cell.x >= width) || cell.x < 0 || cell.y >= height || cell.y < 0) {
            return false;
        }
        if (filledCells.contains(cell) || horizontalClues.containsKey(cell) || verticalClues.containsKey(cell)) {
            return false;
        }
        return true;
    }

    private Map<Cell, Integer> createEmptyAssignments() {
        Map<Cell, Integer> assignments = new HashMap<Cell, Integer>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Cell cell = new Cell(x,y);
                if (isOpenCell(cell)) {
                    assignments.put(cell, null);
                }
            }
        }
        return assignments;
    }

    private List<Cell> getClueCells(Cell clueCell, boolean isHorizontal) {
        List<Cell> cells = new ArrayList<Cell>();
        int x = clueCell.x;
        int y = clueCell.y;
        if (isHorizontal) {
            x += 1;
        }
        else {
            y += 1;
        }
        Cell nextCell = new Cell(x, y);
        while (isOpenCell(nextCell)) {
            cells.add(nextCell);
            if (isHorizontal) {
                x += 1;
            }
            else {
                y += 1;
            }
            nextCell = new Cell(x, y);
        }
        return cells;
    }

    private List<SumAndUniqueConstraint> createSumConstraints() {
        List<SumAndUniqueConstraint> sumAndUniqueConstraints = new ArrayList<SumAndUniqueConstraint>();

        for (Map.Entry<Cell, Integer> entry : horizontalClues.entrySet()) {
            int sum = entry.getValue();
            Cell clueCell = entry.getKey();
            List<Cell> cells = getClueCells(clueCell, true);
            sumAndUniqueConstraints.add(new SumAndUniqueConstraint(sum, cells, true));
        }

        for (Map.Entry<Cell, Integer> entry : verticalClues.entrySet()) {
            int sum = entry.getValue();
            Cell clueCell = entry.getKey();
            List<Cell> cells = getClueCells(clueCell, false);
            sumAndUniqueConstraints.add(new SumAndUniqueConstraint(sum, cells, true));
        }

        return sumAndUniqueConstraints;
    }

    private Board build() {
        Map<Cell, Integer> emptyAssignments = createEmptyAssignments();
        List<SumAndUniqueConstraint> sumAndUniqueConstraints = createSumConstraints();
        
        for (Map.Entry<Cell, Integer> entry : emptyAssignments.entrySet()) {
        	Cell cell = entry.getKey();
        	for (SumAndUniqueConstraint sauc : sumAndUniqueConstraints) {
        		if (sauc.cells.contains(cell)) {
        			cell.setConstraintGroup(sauc);
        		}
        	}
        }

        return new Board(emptyAssignments, sumAndUniqueConstraints);
    }
}
