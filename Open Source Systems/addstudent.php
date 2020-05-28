<?php

include("_includes/config.inc");
include("_includes/dbconnect.inc");
include("_includes/functions.inc");


// check logged in
if (isset($_SESSION['id'])) {

   echo template("templates/partials/header.php");
   echo template("templates/partials/nav.php");

   // Statement of add student
   if (isset($_POST['submit'])) {
	   // Escape special characters, if any
	   $studentid =  mysqli_real_escape_string($conn, $_POST['txtstudentid']);
	   $password =  password_hash(mysqli_real_escape_string($conn, $_POST['txtpassword']),$algo=PASSWORD_DEFAULT, $options=array());
	   $dob =  mysqli_real_escape_string($conn, $_POST['txtdob']);
	   $firstname =  mysqli_real_escape_string($conn, $_POST['txtfirstname']);
	   $lastname =  mysqli_real_escape_string($conn, $_POST['txtlastname']);
	   $house =   mysqli_real_escape_string($conn, $_POST['txthouse']);
	   $town =  mysqli_real_escape_string($conn, $_POST['txttown']);
	   $county =  mysqli_real_escape_string($conn, $_POST['txtcounty']);
	   $country =  mysqli_real_escape_string($conn, $_POST['txtcountry']);
	   $postcode =  mysqli_real_escape_string($conn, $_POST['txtpostcode']);
	   
      $sql = "insert into student values ('$studentid','$password','$dob','$firstname','$lastname','$house','$town','$county','$country','$postcode');";
      $result = mysqli_query($conn, $sql);
      $data['content'] .= "<div class='alert alert-success'>A new record has been added to Student table</div>";
   }
   else  // If a record has not been add
   {

     $data['content'] .= "
	 <div class='container'>
  <h2>Registration form</h2>
	 <form name='addstudent' action='' method='post' >
   <div class='form-group'>
   <input  input name='txtstudentid' class='form-control' placeholder='Studentid' type='text' />
   </div>
   <div class='form-group'>
   <input name='txtpassword' class='form-control' placeholder='Password' type='password' />
      </div>
   <div class='form-group'>
   <input name='txtdob' class='form-control' placeholder='Date of Birth' type='date' />
      </div>
   <div class='form-group'>
   <input name='txtfirstname' class='form-control' placeholder='First Name' type='text' />
      </div>
   <div class='form-group'>
   <input name='txtlastname' class='form-control' placeholder='Surname' type='text'  />
      </div>
   <div class='form-group'>
   <input name='txthouse' class='form-control' placeholder='Number and Street' type='text'  />
      </div>
   <div class='form-group'>
   <input name='txttown' class='form-control' placeholder='Town' type='text'  />
      </div>
   <div class='form-group'>
   <input name='txtcounty' class='form-control' placeholder='County' type='text' />
      </div>
   <div class='form-group'>
   <input name='txtcountry' class='form-control' placeholder='Country' type='text' />
      </div>
   <div class='form-group'>
   <input name='txtpostcode' class='form-control' placeholder='Postcode' type='text' />
      </div>
   <input type='submit' name='submit' class='btn btn-primary btn-form btn-block' value=' ADD ' />";
     $data['content'] .= '</form></div>';
   }

   // render the template
   echo template("templates/default.php", $data);

} else {
   header("Location: index.php");
}

echo template("templates/partials/footer.php");

?>
