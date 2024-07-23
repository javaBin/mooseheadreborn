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
import ActivateParticipantPage from "./pages/ActivateParticipantPage";

function App() {
  return (
    <div>
      <BrowserRouter>
        <Routes>
            <Route path="/" element={<MainListingPage/>} />
            <Route path="/workshop/:workshopId" element={<WorkshopRegistryPage/>} />
            <Route path="/activate/:registerKey" element={<ActivateParticipantPage/>} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
