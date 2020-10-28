package com.misiak.android.autoexpense.database.view

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.database.entity.FuelExpense

@DatabaseView("SELECT * from Car c LEFT JOIN (SELECT f.* FROM (SELECT max(f.expenseDate) AS max_expense_date, carId from FuelExpense f GROUP BY f.carId) sf INNER JOIN FuelExpense f on (f.expenseDate = max_expense_date AND f.carId = sf.carId)) fe ON c.id = fe.carId")
data class CarWithLastFuelExpenseView(
    @Embedded val car: Car,
    @Embedded val fuelExpense: FuelExpense?
)

//select f.* from (select max(f.expense_date) as max_expense_date, car_id from fuel_expense f group by f.car_id) sf
//inner join fuel_expense f on (f.expense_date = max_expense_date AND f.car_id = sf.car_id)

//v2
//SELECT * FROM Car c LEFT Join
//  (SELECT f.* FROM
//      (SELECT max(f.expenseDate) AS max_expense_date, carId from FuelExpense f GROUP BY f.carId) sf
//      INNER JOIN FuelExpense f on (f.expenseDate = max_expense_date AND f.carId = sf.carId))
//  nF on c.id = nf.carId

//v3
//"SELECT * FROM Car c LEFT Join " +
//        "  (SELECT f.* FROM " +
//        "      (SELECT max(f.expenseDate) AS max_expense_date, carId from FuelExpense f GROUP BY f.carId) sf " +
//        "      INNER JOIN FuelExpense f on (f.expenseDate = max_expense_date AND f.carId = sf.carId)) " +
//        "  nF on c.id = nf.carId"