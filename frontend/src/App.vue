<script setup lang="ts">
import { ref } from 'vue'

const navbarOpen = ref(false)

function toggleNavbar() {
  navbarOpen.value = !navbarOpen.value
}
</script>

<template>
  <div>
    <nav class="navbar navbar-expand-lg navbar-dark" role="navigation">
      <div class="container-fluid">
        <a class="navbar-brand" href="/"><span></span></a>
        <button
          class="navbar-toggler"
          type="button"
          :aria-expanded="navbarOpen"
          aria-label="Toggle navigation"
          @click="toggleNavbar()"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div
          id="main-navbar"
          class="collapse navbar-collapse"
          :class="{ show: navbarOpen }"
        >
          <ul class="nav navbar-nav me-auto">
            <li class="nav-item">
              <RouterLink to="/" class="nav-link">
                <span class="fa fa-home" aria-hidden="true"></span> Home
              </RouterLink>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/owners/find">
                <span class="fa fa-search" aria-hidden="true"></span> Find owners
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/vets.html">
                <span class="fa fa-th-list" aria-hidden="true"></span> Veterinarians
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/feedback/new">
                <span class="fa fa-comment" aria-hidden="true"></span> Feedback
              </a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <div class="container-fluid">
      <div class="container xd-container">
        <RouterView />
        <br />
        <br />
        <div class="container">
          <div class="row">
            <div class="col-12 text-center">
              <img
                :src="'/resources/images/spring-logo.svg'"
                alt="Spring Logo"
                class="logo"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>