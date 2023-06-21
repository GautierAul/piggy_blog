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
    <h1 class="text-2xl text-center">Connection</h1>

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
      value="Me connecter"
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
      xhr.open("POST", "/rest/account/login", true);
      xhr.setRequestHeader("Content-Type", "application/json");

      xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
          alert("Vous êtes connecté !");

          sessionStorage.token = xhr.responseText;

          // Decodage du token pour récupérer l'id de l'utilisateur
          var base64Url = xhr.responseText.split(".")[1];
          var base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
          var jsonPayload = decodeURIComponent(
            window
              .atob(base64)
              .split("")
              .map(function (c) {
                return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
              })
              .join("")
          );
          sessionStorage.userId = JSON.parse(jsonPayload).userId;

          window.location.href = "/blog";
        } else if (xhr.readyState === 4 && xhr.status === 401) {
          alert("Mauvais identifiants");
        }
      };

      xhr.send(JSON.stringify(formData));
    });
</script>
