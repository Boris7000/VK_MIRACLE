package com.vkontakte.miracle.engine.view.photoGridView.calculator;

import com.vkontakte.miracle.engine.view.photoGridView.MediaItem;
import com.vkontakte.miracle.engine.view.photoGridView.PhotoGridItem;
import com.vkontakte.miracle.engine.view.photoGridView.PhotoGridPosition;
import com.vkontakte.miracle.model.photos.fields.Size;

import java.util.ArrayList;

public class GridCalculator {

    private final ArrayList<CalculationItem> items = new ArrayList<>();
    private final int count;

    private double maxAspectRatioDelta = 0;
    private int minAspectRatioIndex = -1;
    private int maxNeighbourAspectRatioIndex = -1;

    private float totalVerticalAspectRatio = 0f;

    public GridCalculator(ArrayList<MediaItem> mediaItems){
        count = mediaItems.size();
        for(MediaItem mediaItem:mediaItems){
            CalculationItem calculationItem = new CalculationItem();
            calculationItem.size =  mediaItem.getSizeForWidth(604, false);
            items.add(calculationItem);
        }
    }


    public ArrayList<CalculationItem> calculateGrid(int maxWidth, int maxHeight, int spacing){
        if(count==1){
            calculateForSingle(maxWidth, maxHeight);
        } else {
            if(count==2){
                calculateForDouble(maxWidth, maxHeight, spacing);
            } else {
                if(count==3){
                    calculateForTriple(maxWidth, maxHeight, spacing);
                } else {
                    calculateSymmetric(maxWidth, maxHeight, spacing);
                }
            }
        }
        return items;
    }

    private void calculateForSingle(int maxWidth, int maxHeight){
        CalculationItem calculationItem = items.get(0);
        Size size = calculationItem.size;
        int width;
        int height;

        if(size.getWidth()==size.getHeight()){
            width = Math.min(maxWidth, maxHeight);
            height = width;
        } else {
            double aspect1 = (double) maxWidth/(double) maxHeight;
            double aspect2 = size.getAspectRatio();

            double multiply;
            if(aspect1<aspect2){
                multiply = (double) maxWidth/size.getWidth();
            } else {
                multiply = (double) maxHeight/size.getHeight();
            }

            height = (int) (size.getHeight()*multiply);
            width = (int) (size.getWidth()*multiply);
        }

        PhotoGridPosition photoGridPosition = new PhotoGridPosition();
        photoGridPosition.sizeX = width;
        photoGridPosition.sizeY = height;
        photoGridPosition.marginY = 0;
        photoGridPosition.marginX = 0;
        calculationItem.gridPosition = photoGridPosition;
    }

    private void calculateForDouble(int maxWidth, int maxHeight, int spacing){
        ArrayList<GridRow> rowsHorizontal = createSingleRowGrid();
        ArrayList<GridRow> rowsVertical = createVerticalGrid();

        float aspectRatioDeltaH = Math.abs(calculateTotalVerticalAspectRatio(rowsHorizontal)-1f);
        float aspectRatioDeltaV = Math.abs(calculateTotalVerticalAspectRatio(rowsVertical)-1f);

        if(aspectRatioDeltaH<aspectRatioDeltaV){
            fit(rowsHorizontal, maxWidth, maxHeight, spacing);
        } else {
            fit(rowsVertical, maxWidth, maxHeight, spacing);
        }
    }

    private void calculateForTriple(int maxWidth, int maxHeight, int spacing){
        ArrayList<GridRow> rowsHorizontal = createSingleRowGrid();
        ArrayList<GridRow> rowsVertical = createVerticalGrid();
        ArrayList<GridRow> rowsTop = new ArrayList<>();
        ArrayList<GridRow> rowsBottom = new ArrayList<>();

        GridRow topGridRow = new GridRow();
        topGridRow.addItem(items.get(0));
        topGridRow.addItem(items.get(1));
        GridRow bottomGridRow = new GridRow();
        bottomGridRow.addItem(items.get(2));
        rowsTop.add(topGridRow);
        rowsTop.add(bottomGridRow);

        topGridRow = new GridRow();
        topGridRow.addItem(items.get(0));
        bottomGridRow = new GridRow();
        bottomGridRow.addItem(items.get(1));
        bottomGridRow.addItem(items.get(2));
        rowsBottom.add(topGridRow);
        rowsBottom.add(bottomGridRow);


        float aspectRatioDeltaH = Math.abs(calculateTotalVerticalAspectRatio(rowsHorizontal)-1f);
        float aspectRatioDeltaV = Math.abs(calculateTotalVerticalAspectRatio(rowsVertical)-1f);
        float aspectRatioDeltaT = Math.abs(calculateTotalVerticalAspectRatio(rowsTop)-1f);
        float aspectRatioDeltaB = Math.abs(calculateTotalVerticalAspectRatio(rowsBottom)-1f);

        float minDelta = aspectRatioDeltaH;
        ArrayList<GridRow> rows = rowsHorizontal;

        if(aspectRatioDeltaV<minDelta){
            minDelta = aspectRatioDeltaV;
            rows = rowsVertical;
        }
        if(aspectRatioDeltaT<minDelta){
            minDelta = aspectRatioDeltaT;
            rows = rowsTop;
        }
        if(aspectRatioDeltaB<minDelta){
            rows = rowsBottom;
        }
        fit(rows, maxWidth, maxHeight, spacing);
    }

    private void calculateSymmetric(int maxWidth, int maxHeight, int spacing){
        ArrayList<GridRow> rowsHorizontal = createSingleRowGrid();
        ArrayList<GridRow> rowsVertical = createVerticalGrid();
        double doubleRowCount = Math.sqrt(rowsHorizontal.get(0).aspectRatio);
        ArrayList<GridRow> rowsStandard = createStandardGrid(doubleRowCount);
        ArrayList<GridRow> rowsSymmetric = createSymmetricGrid(doubleRowCount);

        float aspectRatioDeltaH = Math.abs(calculateTotalVerticalAspectRatio(rowsHorizontal)-1f);
        float aspectRatioDeltaV = Math.abs(calculateTotalVerticalAspectRatio(rowsVertical)-1f);
        float aspectRatioDeltaST = Math.abs(calculateTotalVerticalAspectRatio(rowsStandard)-1f);
        float aspectRatioDeltaSY = Math.abs(calculateTotalVerticalAspectRatio(rowsSymmetric)-1f);

        float minDelta = aspectRatioDeltaH;
        ArrayList<GridRow> rows = rowsHorizontal;

        if(aspectRatioDeltaV<minDelta){
            minDelta = aspectRatioDeltaV;
            rows = rowsVertical;
        }
        if(aspectRatioDeltaST<minDelta){
            minDelta = aspectRatioDeltaST;
            rows = rowsStandard;
        }
        if(aspectRatioDeltaSY<minDelta){
            rows = rowsSymmetric;
        }

        totalVerticalAspectRatio = calculateTotalVerticalAspectRatio(rows);

        //replacing
        if(rows.size()>1 && totalVerticalAspectRatio>1.5f){

            // first finding replace
            findReplace(rows, doubleRowCount);

            while (maxNeighbourAspectRatioIndex>-1&&minAspectRatioIndex>-1&&totalVerticalAspectRatio>1.5f){

                GridRow minRow = rows.get(minAspectRatioIndex);
                GridRow maxRow = rows.get(maxNeighbourAspectRatioIndex);

                int removeIndex;
                int putIndex;

                if(minAspectRatioIndex>maxNeighbourAspectRatioIndex){
                    removeIndex = maxRow.size() - 1;
                    putIndex = 0;
                } else {
                    removeIndex = 0;
                    putIndex = minRow.size();
                }

                CalculationItem removeItem = maxRow.items.get(removeIndex);

                minRow.addItem(putIndex, removeItem);
                if(maxRow.size()>1) {
                    maxRow.removeItem(removeItem);
                } else {
                    rows.remove(maxRow);
                }

                maxAspectRatioDelta = 0;
                minAspectRatioIndex = -1;
                maxNeighbourAspectRatioIndex = -1;

                totalVerticalAspectRatio = calculateTotalVerticalAspectRatio(rows);

                findReplace(rows, doubleRowCount);
            }
        }

        fit(rows, maxWidth, maxHeight, spacing);
    }

    private ArrayList<GridRow> createSingleRowGrid(){
        ArrayList<GridRow> rowsHorizontal = new ArrayList<>();
        GridRow singleGridRow = new GridRow();
        for(CalculationItem calculationItem:items){
            singleGridRow.addItem(calculationItem);
        }
        rowsHorizontal.add(singleGridRow);
        return rowsHorizontal;
    }

    private ArrayList<GridRow> createVerticalGrid(){
        ArrayList<GridRow> rowsVertical = new ArrayList<>();
        for(CalculationItem calculationItem:items){
            GridRow gridRow = new GridRow();
            gridRow.addItem(calculationItem);
            rowsVertical.add(gridRow);
        }
        return rowsVertical;
    }

    private ArrayList<GridRow> createStandardGrid(double doubleRowCount){
        ArrayList <GridRow> rowsStandard = new ArrayList<>();
        int added = 0;
        while (added<items.size()){
            GridRow gridRow = new GridRow();
            while (added<items.size()){
                gridRow.addItem(items.get(added++));
                if(gridRow.aspectRatio>=doubleRowCount){
                    break;
                }
            }
            rowsStandard.add(gridRow);
        }
        return rowsStandard;
    }

    private ArrayList<GridRow> createSymmetricGrid(double doubleRowCount){
        ArrayList <GridRow> rowsSymmetric = new ArrayList<>();
        int rowsCount = (int) Math.round(doubleRowCount);
        int itemsInRowCount = count/rowsCount;
        int totalItemsDelta = 0;

        if(itemsInRowCount==1){
            rowsCount+=count%rowsCount;
        } else {
            totalItemsDelta = count%rowsCount;
        }

        int added = 0;
        for(int i=0; i<rowsCount; i++){
            GridRow gridRow = new GridRow();
            int totalInRow = itemsInRowCount;
            if(totalItemsDelta>0){
                totalInRow++;
                totalItemsDelta--;
            }
            for(int j=0; j<totalInRow;j++){
                gridRow.addItem(items.get(added++));
            }
            rowsSymmetric.add(gridRow);
        }
        return rowsSymmetric;
    }

    private float calculateTotalVerticalAspectRatio(ArrayList<GridRow> rows){
        float totalVerticalAspectRatio = 0;
        for (GridRow gridRow:rows){
            totalVerticalAspectRatio+=gridRow.getVerticalAspectRatio();
        }
        return totalVerticalAspectRatio;
    }

    private void findReplace(ArrayList<GridRow> rows, double doubleRowCount){

        for(int index = 0; index<rows.size();index++){
            GridRow gridRow = rows.get(index);

            float aspectRatio = gridRow.aspectRatio;
            float verticalAspectRatio = gridRow.getVerticalAspectRatio();

            CalculationItem item1=null;
            CalculationItem item2=null;
            CalculationItem item3=null;
            CalculationItem item4=null;

            if(index-1>=0){
                GridRow gridRow1 = rows.get(index-1);
                item1 = gridRow1.items.get(gridRow1.size() - 1);
                item3 = gridRow.items.get(0);
            }

            if(index+1<=rows.size()-1){
                GridRow gridRow1 = rows.get(index+1);
                item2 = gridRow1.items.get(0);
                item4 = gridRow.items.get(gridRow.size() - 1);
            }

            if(item1!=null){

                GridRow gridRow1 = rows.get(index-1);
                double itemAspectRatio = item1.size.getAspectRatio();

                float newVerticalAspectRatio = totalVerticalAspectRatio;
                newVerticalAspectRatio-=gridRow1.getVerticalAspectRatio();
                newVerticalAspectRatio-=verticalAspectRatio;

                if(gridRow1.size()>1) {
                    newVerticalAspectRatio += 1f / (gridRow1.aspectRatio - itemAspectRatio);
                }
                newVerticalAspectRatio+=1f/(aspectRatio+itemAspectRatio);

                float verticalDelta = totalVerticalAspectRatio - newVerticalAspectRatio;

                if(newVerticalAspectRatio<totalVerticalAspectRatio&&verticalDelta>0.01f) {

                    if (verticalDelta > maxAspectRatioDelta) {
                        if (gridRow1.size() == 1) {
                            boolean canReplace = itemAspectRatio < doubleRowCount;
                            if (canReplace) {
                                maxAspectRatioDelta = verticalDelta;
                                minAspectRatioIndex = index;
                                maxNeighbourAspectRatioIndex = index - 1;
                            }
                        } else {
                            maxAspectRatioDelta = verticalDelta;
                            minAspectRatioIndex = index;
                            maxNeighbourAspectRatioIndex = index - 1;
                        }
                    }
                }
            }

            if(item2!=null){

                GridRow gridRow1 = rows.get(index+1);
                double itemAspectRatio = item2.size.getAspectRatio();

                float newVerticalAspectRatio = totalVerticalAspectRatio;
                newVerticalAspectRatio-=gridRow1.getVerticalAspectRatio();
                newVerticalAspectRatio-=verticalAspectRatio;

                if(gridRow1.size()>1) {
                    newVerticalAspectRatio += 1f / (gridRow1.aspectRatio - itemAspectRatio);
                }
                newVerticalAspectRatio += 1f / (aspectRatio + itemAspectRatio);

                float verticalDelta = totalVerticalAspectRatio - newVerticalAspectRatio;

                if(newVerticalAspectRatio<totalVerticalAspectRatio&&verticalDelta>0.01f) {

                    if (verticalDelta > maxAspectRatioDelta) {
                        if (gridRow1.size() == 1) {
                            boolean canReplace = itemAspectRatio < doubleRowCount;
                            if (canReplace) {
                                maxAspectRatioDelta = verticalDelta;
                                minAspectRatioIndex = index;
                                maxNeighbourAspectRatioIndex = index + 1;
                            }
                        } else {
                            maxAspectRatioDelta = verticalDelta;
                            minAspectRatioIndex = index;
                            maxNeighbourAspectRatioIndex = index + 1;
                        }
                    }
                }
            }

            if(item3!=null){

                GridRow gridRow1 = rows.get(index-1);
                double itemAspectRatio = item3.size.getAspectRatio();

                float newVerticalAspectRatio = totalVerticalAspectRatio;
                newVerticalAspectRatio-=gridRow1.getVerticalAspectRatio();
                newVerticalAspectRatio-=verticalAspectRatio;

                newVerticalAspectRatio += 1f / (gridRow1.aspectRatio - itemAspectRatio);
                if(gridRow.size()>1) {
                    newVerticalAspectRatio += 1f / (aspectRatio + itemAspectRatio);
                }

                float verticalDelta = totalVerticalAspectRatio - newVerticalAspectRatio;

                if(newVerticalAspectRatio<totalVerticalAspectRatio&&verticalDelta>0.01f) {

                    if (verticalDelta > maxAspectRatioDelta) {
                        if (gridRow.size() == 1) {
                            boolean canReplace = itemAspectRatio < doubleRowCount;
                            if (canReplace) {
                                maxAspectRatioDelta = verticalDelta;
                                minAspectRatioIndex = index - 1;
                                maxNeighbourAspectRatioIndex = index;
                            }
                        } else {
                            maxAspectRatioDelta = verticalDelta;
                            minAspectRatioIndex = index - 1;
                            maxNeighbourAspectRatioIndex = index;

                        }
                    }
                }
            }

            if(item4!=null){

                GridRow gridRow1 = rows.get(index+1);
                double itemAspectRatio = item4.size.getAspectRatio();

                float newVerticalAspectRatio = totalVerticalAspectRatio;
                newVerticalAspectRatio-=gridRow1.getVerticalAspectRatio();
                newVerticalAspectRatio-=verticalAspectRatio;

                newVerticalAspectRatio+=1f/(gridRow1.aspectRatio+itemAspectRatio);
                if(gridRow.size()>1) {
                    newVerticalAspectRatio += 1f / (aspectRatio - itemAspectRatio);
                }

                float verticalDelta = totalVerticalAspectRatio - newVerticalAspectRatio;

                if(newVerticalAspectRatio<totalVerticalAspectRatio&&verticalDelta>0.01f) {

                    if (verticalDelta > maxAspectRatioDelta) {
                        if (gridRow.size() == 1) {
                            boolean canReplace = itemAspectRatio < doubleRowCount;
                            if (canReplace) {
                                maxAspectRatioDelta = verticalDelta;
                                minAspectRatioIndex = index + 1;
                                maxNeighbourAspectRatioIndex = index;
                            }
                        } else {
                            maxAspectRatioDelta = verticalDelta;
                            minAspectRatioIndex = index + 1;
                            maxNeighbourAspectRatioIndex = index;
                        }
                    }
                }
            }
        }
    }

    private void fit(ArrayList<GridRow> rows, int maxWidth, int maxHeight, int spacing){

        int rowsHeightPxSum = 0;

        for(GridRow gridRow:rows){

            int rowWidthPxSum = 0;
            int rowHeight = 0;

            for(CalculationItem calculationItem:gridRow.items){

                Size size = calculationItem.size;

                if(rowHeight==0){
                    rowHeight = size.getHeight();
                }

                double xMultiply = (double) rowHeight/size.getHeight();
                int newImageWidth = (int) (size.getWidth()*xMultiply);

                PhotoGridPosition photoGridPosition = new PhotoGridPosition();
                photoGridPosition.sizeX = newImageWidth;
                photoGridPosition.sizeY = rowHeight;
                calculationItem.gridPosition = photoGridPosition;
                rowWidthPxSum+=newImageWidth;

            }

            double rowMultiply = (double) (maxWidth-((gridRow.size()-1)*spacing))/(double) rowWidthPxSum;
            rowHeight*=rowMultiply;
            int marginX = 0;

            for(CalculationItem calculationItem:gridRow.items){
                PhotoGridPosition photoGridPosition = calculationItem.gridPosition;
                photoGridPosition.sizeX*=rowMultiply;
                photoGridPosition.sizeY = rowHeight;
                photoGridPosition.marginY = rowsHeightPxSum;
                photoGridPosition.marginX = marginX;
                marginX+=spacing+photoGridPosition.sizeX;
            }
            rowsHeightPxSum+=rowHeight+spacing;
        }

        if(rowsHeightPxSum>maxHeight){
            double multiply = (double) maxHeight/rowsHeightPxSum;
            fit(rows,(int)(maxWidth*multiply), maxHeight, spacing);
        }

    }

    private static class GridRow{

        private float aspectRatio = 0;

        private final ArrayList<CalculationItem> items = new ArrayList<>();

        public void addItem(int index, CalculationItem item){
            aspectRatio+=item.size.getAspectRatio();
            items.add(index, item);
        }

        public void addItem(CalculationItem item){
            aspectRatio+=item.size.getAspectRatio();
            items.add(item);
        }
        public void removeItem(CalculationItem item){
            aspectRatio-=item.size.getAspectRatio();
            items.remove(item);
        }

        public int size(){
            return items.size();
        }

        public float getVerticalAspectRatio(){
            return 1f/aspectRatio;
        }
    }
}
