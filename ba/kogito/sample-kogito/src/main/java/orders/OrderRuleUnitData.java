package orders;

import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.RuleUnitData;

import model.Order;

public class OrderRuleUnitData implements RuleUnitData {

    DataStore<Order> orders = DataSource.createStore();

    public DataStore<Order> getOrders() {
        return orders;
    }
}
