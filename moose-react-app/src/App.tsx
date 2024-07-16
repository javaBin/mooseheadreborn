import React from 'react';
import './App.css';
import {
  BrowserRouter,
  Routes,
  Route,
} from "react-router-dom";
import MainListingPage from "./pages/MainListingPage";
import 'bootstrap/dist/css/bootstrap.min.css';
import WorkshopRegistryPage from "./pages/WorkshopRegistryPage";

function App() {
  return (
    <div>
      <BrowserRouter>
        <Routes>
            <Route path="/" element={<MainListingPage/>} />
            <Route path="/workshop/:workshopId" element={<WorkshopRegistryPage/>} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
