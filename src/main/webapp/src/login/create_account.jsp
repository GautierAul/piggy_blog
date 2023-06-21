<%@ page pageEncoding="UTF-8" %> <%@include file="../../head.jsp" %> <%@include
file="../../header.jsp" %>
<div class="main flex flex-col items-center justify-center grow gap-2">
  <form
    name="loginForm"
    method="post"
    action="/rest/account/create"
    class="flex flex-col gap-2"
    id="loginForm"
  >
    <h1 class="text-2xl text-center">Créer un compte</h1>
    <div class="flex flex-col gap-2">
      <span>Nom d'utilisateur :</span>
      <input type="text" name="username" class="rounded-lg p-2 text-black" />
    </div>
    <div class="flex flex-col gap-2">
      <span>Mot de passe :</span>
      <input
        type="password"
        name="password"
        class="rounded-lg p-2 text-black"
      />
    </div>
    <input
      type="submit"
      value="Créer mon compte"
      class="border border-white rounded-lg px-2 py-1"
    />
  </form>
</div>

<script>
  document
    .getElementById("loginForm")
    .addEventListener("submit", function (event) {
      event.preventDefault();

      var formData = {
        username: document.querySelector('input[name="username"]').value,
        password: document.querySelector('input[name="password"]').value,
      };
      var xhr = new XMLHttpRequest();
      xhr.open("POST", "/rest/account/create", true);
      xhr.setRequestHeader("Content-Type", "application/json");

      xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
          window.location.href = "/login";
        } else if (xhr.readyState === 4 && xhr.status === 409) {
          alert("Ce nom d'utilisateur est déjà pris !");
        }
      };

      xhr.send(JSON.stringify(formData));
    });
</script>
