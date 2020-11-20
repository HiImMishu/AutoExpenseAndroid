<img src = "photos\logo_v1.png" width="300"></img>
<h3> Android App for tracking car expenses.</h3>
<p>
    Current version supports storing information about Cars and adding Fuel Expenses.
</p>
<p>
    App sends data to server via my REST API. Server stores data in MySQL database and performs basic calculations like average fuel consumption or cost / 100km.</br>
    Data is also stored in local Cache (Room database) in case there is no internet connection.
</p>
<p>
    This is product of my first experience with Android SDK and Kotlin. I needed front for my Rest APi builded on top of a Springboot.</br>
    <a href="https://github.com/HiImMishu/AutoExpense_SpringRestAPI" target="_blank">Click here to visit my Rest API repository</a>
</p>
<p>
    App build in MVVM architecture</br>
    Room Database for local Cache</br>
    Retrofit for API Calls</br>
    Navigation UI foor navigation</br>
    Material.io for design elements</br>
</p>

<h3>Take a look at screenshots from App with short descripiton</h3>
<table style="width: 100%">
    <tr style="text-align:center">
        <th width= "50%"> Descripton </th>
        <th width= "50%"> Screenshot </th>
    </tr>
    <tr>
        <td> Users uses Google Account to Sign In. API calls contains Google ID Token to Authenticate user on server side. In future I will be working on OAuth Authentication Server so that users can create accounts or Sign In with SSO (Facebook, Google, etc.) </td>
        <td style="text-align: center"> <img src = "photos\sign_up_screen.png"></img> </td>
    </tr>
    <tr>
        <td> When user has not yet added any cars, there will be placeholder as  shown on screenshot.</td>
        <td style="text-align: center"> <img src = "photos\main_screen_no_items.png"></img> </td>
    </tr>
    <tr>
        <td> Each car is presented as a card in scrollable Recycler View. Cards can be swiped left to delete, right to edit or clicked to go to next Fragment containing car information and expenses.</br>
        Average fuel consumptions shown within each car are calculated from most recent fuel expense.</br>
        Add Car button is a floating button with fixed position.
        </td>
        <td style="text-align: center"> <img src = "photos\main_screen_with_items.png"></img> </td>
    </tr>
    <tr>
        <td> When user swipes right on car app navigates to editing car view.</td>
        <td style="text-align: center"> <img src = "photos\edit_car_swipe.png"></img> </td>
    </tr>
    <tr>
        <td> When user is taken to edit car view form is prefilled with current data.</td>
        <td style="text-align: center"> <img src = "photos\edit_car.png"></img> </td>
    </tr>
    <tr>
        <td> When user taps Add Car he is taken to suitable view.</td>
        <td style="text-align: center"> <img src = "photos\add_car.png"></img> </td>
    </tr>
    <tr>
        <td> When user tries to submit invalid data he is presented with suitable message.</td>
        <td style="text-align: center"> <img src = "photos\form_validation_error.png"></img> </td>
    </tr>
    <tr>
        <td> When user swipes card to the left car is being deleted with all of it's data.</td>
        <td style="text-align: center"> <img src = "photos\delete_car_swipe.png"></img> </td>
    </tr>
    <tr>
        <td> When user taps on card App navigates to view containing car information. Recycler view contains all fuel expense of presented car. User can add new expense by tapping floating "Add Fuel Expense" button. Fuel Expenses also can by edited and deleted on swipe.</td>
        <td style="text-align: center"> <img src = "photos\car_information_screen.png"></img> </td>
    </tr>
    <tr>
        <td> By tapping Engine Information users is presented with information about car's engine and button to edit Engine data.</td>
        <td style="text-align: center"> <img src = "photos\car_information_screen_with_engine.png"></img> </td>
    </tr>
    <tr>
        <td> If car has no Engine added, suitable button is presented.</td>
        <td style="text-align: center"> <img src = "photos\car_information_no_engine.png"></img> </td>
    </tr>
    <tr>
        <td> Adding engine screen.</td>
        <td style="text-align: center"> <img src = "photos\add_engine.png"></img> </td>
    </tr>
    <tr>
        <td> Editing engine screen.</td>
        <td style="text-align: center"> <img src = "photos\edit_engine.png"></img> </td>
    </tr>
    <tr>
        <td> Fuel Expense can be swiped left to delete.</td>
        <td style="text-align: center"> <img src = "photos\delete_fuel_expense_swipe.png"></img> </td>
    </tr>
    <tr>
        <td> Right swiping Fuel Expnse leads to editing.</td>
        <td style="text-align: center"> <img src = "photos\edit_ful_expense_swipe.png"></img> </td>
    </tr>
    <tr>
        <td> When users taps on Add Fuel Expense button he is presented with Form. Date is prefilled by application but it can be edited.</br>
        All fields are validated by App. When user wnters invalid data he is presented with suitable error message.</td>
        <td style="text-align: center"> <img src = "photos\add_fuel_expense.png"></img> </td>
    </tr>
    <tr>
        <td> Toolbar contains Dropdown menu. When expaded user is presented with Sign Out option.</br>
        When user Signs Out app clears it's backstack and takes User to Sign In Screen. Tapping system back button would leave App insted of undoing Sign Out.</br>
        Toolbar also has Navigate Up button.</td>
        <td style="text-align: center"> <img src = "photos\edit_engine.png"></img> </td>
    </tr>
</table>