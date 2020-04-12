package logic;

import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.GoalType;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class MainForm extends JFrame {


    private JButton button1;
    private JPanel panel1;
    private JTable table1;
    private JScrollPane scrollPan1;
    private JButton button2;
    private JButton button3;
    private JButton button4;

    private InputData inputData;

    public MainForm() {
        setContentPane(panel1);

        setPreferredSize(new Dimension(800, 600));
        pack();
        setLocationRelativeTo(null);
        setTitle("SIMUS standalone");
        setVisible(true);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                // start of code for horizontal headers
                ListModel lm = new AbstractListModel() {
                    String headers[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i"};

                    public int getSize() {
                        return headers.length;
                    }

                    public Object getElementAt(int index) {
                        return headers[index];
                    }
                };

                JList rowHeader = new JList(lm);
                rowHeader.setFixedCellWidth(50);
                rowHeader.setCellRenderer(new RowHeaderRenderer(table1));
                scrollPan1.setRowHeaderView(rowHeader);
                // end of code for horizontal headers


                inputData = Init.generateRndData(new ConstraintData(4, 5, 0, 100, 5000, 70000), 2);
                TableModel model = new InputDataTableModel(inputData);
                table1.setModel(model);

                System.out.println("generate");
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SIMUS simus = new SIMUS(inputData);
                boolean res = simus.runLogic();
                System.out.println(res);
                Rank[] ranks = simus.getRanks();

                StringBuilder stringRanks = new StringBuilder();
                for (int i = 0; i < ranks.length; i++) {
                    System.out.println(ranks[i].minRank + "-" + ranks[i].maxRank);
                    stringRanks.append(ranks[i].minRank).append("-").append(ranks[i].maxRank).append('\n');
                }

                JOptionPane.showMessageDialog(MainForm.this, stringRanks);
            }
        });

        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // TODO: получить iosaConstraint
                IOSAConstraint iosaConstraint = new IOSAConstraint(null, null, null, null); // TODO: передать 2 idm, 2 rhs;
                IOSAResult iosaResult = SIMUS.runIOSA(inputData, iosaConstraint, 5000, 500);
                if(iosaResult.getIsSuccess()){
                    // TODO: отобразить результат
                } else {
                    // TODO: Сообщить юзеру, что успех метода на рандомных данных < 500/5000
                }
            }
        });
    }

    public static void main(String[] args) {
        new MainForm();
    }

    class RowHeaderRenderer extends JLabel implements ListCellRenderer {

        RowHeaderRenderer(JTable table) {
            JTableHeader header = table.getTableHeader();
            setOpaque(true);
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            setHorizontalAlignment(CENTER);
            setForeground(header.getForeground());
            setBackground(header.getBackground());
            setFont(header.getFont());
        }

        public Component getListCellRendererComponent(JList list,
                                                      Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }


    public class InputDataTableModel implements TableModel {

        private Set<TableModelListener> listeners = new HashSet<TableModelListener>();
        private InputData inputData;

        public InputDataTableModel(InputData inputData) {
            this.inputData = inputData;
        }

        @Override
        public int getRowCount() {
            return inputData.criteriaCount();
        }

        @Override
        public int getColumnCount() {
            return inputData.alternativeCount() + 3; //idm + rhsSign + rhs + actions
        }

        @Override
        public String getColumnName(int columnIndex) {
            if (columnIndex == inputData.alternativeCount()) //rhs sign
                return "rhs sign";
            else if (columnIndex == inputData.alternativeCount() + 1)
                return "rhs";
            else if (columnIndex == inputData.alternativeCount() + 2)
                return "action";
            else
                return "Alternative " + (char) ('A' + columnIndex);

        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == inputData.alternativeCount()) //rhs sign
                return String.class;
            else if (columnIndex == inputData.alternativeCount() + 1)
                return Double.class;
            else if (columnIndex == inputData.alternativeCount() + 2)
                return String.class;
            else
                return Double.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == inputData.alternativeCount()) //rhs sign
                return inputData.rhsSigns[rowIndex].toString();
            else if (columnIndex == inputData.alternativeCount() + 1)
                return inputData.rhs[rowIndex];
            else if (columnIndex == inputData.alternativeCount() + 2)
                return inputData.actions[rowIndex].toString();
            else
                return inputData.idm[rowIndex][columnIndex];
        }

        @Override
        public void setValueAt(Object o, int rowIndex, int columnIndex) {
            if (columnIndex == inputData.alternativeCount())
                inputData.rhsSigns[rowIndex] = (ConsType) o;
            else if (columnIndex == inputData.alternativeCount() + 1)
                inputData.rhs[rowIndex] = Double.parseDouble(o.toString());
            else if (columnIndex == inputData.alternativeCount() + 2)
                inputData.actions[rowIndex] = (GoalType) o;
            else
                inputData.idm[rowIndex][columnIndex] = Double.parseDouble(o.toString());
        }

        @Override
        public void addTableModelListener(TableModelListener tableModelListener) {
            listeners.add(tableModelListener);
        }

        @Override
        public void removeTableModelListener(TableModelListener tableModelListener) {
            listeners.remove(tableModelListener);
        }
    }
}
