<%@ page pageEncoding="UTF-8" %> <%@include file="../../head.jsp" %> <%@include
file="../../header.jsp" %>

<div class="main flex flex-col items-center justify-center grow gap-2">
  <form
    name="createBlog"
    method="post"
    action="/blog"
    class="flex flex-col gap-2 w-1/3"
    id="createBlog"
  >
    <h1 class="text-2xl text-center">Créer un piggy</h1>
    <div class="flex flex-col gap-2">
      <label for="title">Titre:</label>
      <input type="text" name="title" class="rounded-lg p-2 text-black" />
    </div>
    <div class="flex flex-col gap-2">
      <label for="content">Contenu :</label>
      <textarea name="content" rows="11" class="rounded-lg p-2 text-black"  placeholder="Ecrivez ce qui passe par votre tete"></textarea>
    <input
      type="submit"
      value="Créer mon piggy ;)"
      class="border border-white rounded-lg px-2 py-1"
    />
    <input type="hidden" id="createdBy" name="createdBy" class="rounded-lg p-2 text-black" />

  </form>
</div>
<script>
    document.getElementById("createdBy").value = sessionStorage.getItem("userId");
</script>