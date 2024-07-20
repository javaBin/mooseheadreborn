import {WorkshopType} from "./ServerTypes";

const ServerConfig = {
    //address: "",
    address: "http://localhost:8080",
    readWorkshopFromServer: (workshopId: string):Promise<WorkshopType|String> => {
        return new Promise((resolve, reject) => {
            fetch(ServerConfig.address + "/api/workshop/" + workshopId)//
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        return response.text().then(errorText => {
                            throw new Error(errorText);
                        });

                    }
                }).then(json => resolve(json))
                .catch(error => {
                    resolve(error.message);
                });

        })
    }
};
export default ServerConfig;