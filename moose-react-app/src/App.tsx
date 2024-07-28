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
import RegistrationViewPage from "./pages/RegistrationViewPage";

function App() {
  return (
    <div>
      <BrowserRouter>
        <Routes>
            <Route path="/" element={<MainListingPage/>} />
            <Route path="/workshop/:workshopId" element={<WorkshopRegistryPage/>} />
            <Route path="/activate/:registerKey" element={<ActivateParticipantPage/>} />
            <Route path="/registration/:registrationId" element={<RegistrationViewPage/>} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
