package utility;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * JavaFX equivalent of the Swing EditableCellFocusAction. Installs Tab-key navigation on a
 * {@link TableView} that skips non-editable columns and invokes a callback to add a new row when
 * the cursor wraps past the last cell.
 *
 * <p>Usage:
 * <pre>
 *   EditableCellFocusAction.install(tableView, () -> model.addRow());
 * </pre>
 */
public class EditableCellFocusAction {

  /** Callback invoked when the user tabs past the last editable cell in the table. */
  public interface TableRowAdder {
    /**
     * Add a new row to the table model.
     *
     * @return {@code true} if a row was successfully added
     */
    boolean addRow();
  }

  /**
   * Installs Tab-key focus navigation on the given {@link TableView}.  Navigation skips
   * non-editable columns and triggers {@code rowAdder} when the last cell is reached.
   *
   * @param <T>      the table row type
   * @param table    the target TableView
   * @param rowAdder callback that appends a new row; may be {@code null} to disable auto-append
   */
  public static <T> void install(TableView<T> table, TableRowAdder rowAdder) {
    table.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
      if (event.getCode() != KeyCode.TAB) return;

      int originalRow = table.getSelectionModel().getSelectedIndex();
      int originalCol = table.getFocusModel().getFocusedCell().getColumn();

      // Move to the next cell
      navigateNext(table);
      event.consume();

      int row = table.getSelectionModel().getSelectedIndex();
      int col = table.getFocusModel().getFocusedCell().getColumn();

      // Skip over non-editable columns (bounded to prevent infinite loops)
      int maxSteps = table.getItems().size() * Math.max(1, table.getColumns().size());
      int steps = 0;
      while (steps++ < maxSteps && !isCellEditable(table, row, col)) {
        navigateNext(table);
        int newRow = table.getSelectionModel().getSelectedIndex();
        int newCol = table.getFocusModel().getFocusedCell().getColumn();

        // No movement – reset selection and stop
        if (newRow == row && newCol == col) {
          table.getSelectionModel().select(originalRow);
          if (!table.getColumns().isEmpty()) {
            table.getFocusModel().focus(originalRow,
                table.getColumns().get(Math.min(originalCol, table.getColumns().size() - 1)));
          }
          break;
        }

        // Wrapped all the way back to start – stop
        if (newRow == originalRow && newCol == originalCol) break;

        row = newRow;
        col = newCol;
      }

      // If we are at (0, 0) and a rowAdder is provided, append a new row
      if (col == 0 && row == 0 && rowAdder != null) {
        if (rowAdder.addRow()) {
          int lastRow = table.getItems().size() - 1;
          table.getSelectionModel().select(lastRow);
          if (!table.getColumns().isEmpty()) {
            table.getFocusModel().focus(lastRow, table.getColumns().get(0));
          }
        }
      }
    });
  }

  /** Advances selection to the next cell, wrapping around at the end of each row/table. */
  private static <T> void navigateNext(TableView<T> table) {
    int row = table.getSelectionModel().getSelectedIndex();
    int col = table.getFocusModel().getFocusedCell().getColumn();
    int colCount = table.getColumns().size();
    int rowCount = table.getItems().size();

    col++;
    if (col >= colCount) {
      col = 0;
      row++;
    }
    if (row >= rowCount) {
      row = 0;
      col = 0;
    }

    table.getSelectionModel().select(row);
    if (col < colCount) {
      table.getFocusModel().focus(row, table.getColumns().get(col));
    }
  }

  /** Returns {@code true} if the column at {@code col} is editable and the row exists. */
  private static <T> boolean isCellEditable(TableView<T> table, int row, int col) {
    if (row < 0 || col < 0) return false;
    if (row >= table.getItems().size()) return false;
    if (col >= table.getColumns().size()) return false;
    TableColumn<T, ?> column = table.getColumns().get(col);
    return table.isEditable() && column.isEditable();
  }

}
