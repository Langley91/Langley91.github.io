<?php if (!empty($message)) {
echo "<div class='alert alert-danger alert-dismissible fade show'>$message</div>"; } ?>
  <div class="container">
    <div class="row">
      <div class="col-sm-9 col-md-7 col-lg-5 mx-auto">
        <div class="card card-signin my-5">
          <div class="card-body">
            <h5 class="card-title text-center">Sign In</h5>
			<form name="frmLogin" action="authenticate.php" method="post" class="form-signin">
			   Student ID:
			   <input name="txtid" type="text" class='form-control' />
			   <br/>
			   Password:
			   <input name="txtpwd" type="password" class='form-control' />
			   <br/>
			   <input type="submit" value="Login" name="btnlogin" class="btn btn-lg btn-primary btn-block" />
			</form>
          </div>
        </div>
      </div>
    </div>
  </div>