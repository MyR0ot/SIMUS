package logic;

import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.GoalType;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.dnd.DropTarget;
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
    private JButton button4;
    private JSpinner spinner1;
    private JSpinner spinner2;

    private InputData inputData;

    private int maxCriteriaCount = 10;
    private int maxAlternativeCount = 10;

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

                    public int getSize() { return inputData.criteriaCount(); }

                    public Object getElementAt(int index) {
                        return "C-"+ index;
                    }
                };

                JList rowHeader = new JList(lm);
                rowHeader.setFixedCellWidth(50);
                rowHeader.setCellRenderer(new RowHeaderRenderer(table1));
                rowHeader.setFixedCellHeight(table1.getRowHeight());
                scrollPan1.setRowHeaderView(rowHeader);
                // end of code for horizontal headers

                int criteriaCount = (int)spinner1.getValue();
                int alternativeCount = (int)spinner2.getValue();


                inputData = Init.generateRndData(new ConstraintData(criteriaCount, alternativeCount, 0, 100, 5000, 14000*alternativeCount), 2);
                TableModel model = new InputDataTableModel(inputData);
                table1.setModel(model);

                JComboBox b = new JComboBox(new String[] {">","<",">=","<=","="});

                table1.getColumnModel().getColumn(inputData.alternativeCount() ).setCellEditor(new DefaultCellEditor(b));

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
                IOSAConstraint iosaConstraint = new IOSAConstraint(
                        multIDM(inputData.idm, 0.95),
                        multIDM(inputData.idm, 1.05),
                        inputData.rhs,
                        inputData.rhs); // TODO: передать 2 idm, 2 rhs;

                IOSAResult iosaResult = SIMUS.runIOSA(inputData, iosaConstraint, 20, 0); // TODO: настроить testCount, successCountMin
                System.err.println("successCount = " + iosaResult.getSuccessCount());
                if(iosaResult.getIsSuccess()){
                    // TODO: отобразить результат
                    iosaResult.printPMatrix();

                } else {
                    // TODO: Сообщить юзеру, что успех метода на рандомных данных < 500/5000
                }
            }
        });
    }

    public static void main(String[] args) {
        new MainForm();
    }

    @Deprecated
    public static double[][] multIDM(double[][] idm, double kef){
        double[][] res = idm.clone();
        for (int i = 0; i < idm.length; i++) {
            res[i] = idm[i].clone();
        }

        for(int i = 0; i < res.length; i++)
            for(int j = 0; j< res[i].length; j++)
                res[i][j]*=kef;

        return res;
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

    private String consTypeToStr(ConsType type){
        switch (type)
        {
            case LE:
                return "<=";
            case GE:
                return ">=";
            case LOWER:
                return "<";
            case UPPER:
                return ">";
            case EQ:
                return "=";
            default:
                return null;
        }

    }

    private ConsType strToConsType(String str){
        switch (str)
        {
            case "<=":
                return ConsType.LE;
            case ">=":
                return ConsType.GE;
            case "<":
                return ConsType.LOWER;
            case ">":
                return ConsType.UPPER;
            case "=":
                return ConsType.EQ;
            default:
                return null;
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
                return "A-" + columnIndex;

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
                return consTypeToStr(inputData.rhsSigns[rowIndex]);
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
                inputData.rhsSigns[rowIndex] = strToConsType((String)o);
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
