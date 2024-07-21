import {WorkshopType} from "../ServerTypes";
import ServerConfig from "../ServerConfig";

interface WorkshopFromServer {
    workshop: WorkshopType|null;
    errormessage: string|null;
}

const readWorkshopFromServer =  (workshopId: string):Promise<WorkshopFromServer> => {
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
            }).then(json => resolve({workshop : json,errormessage:null}))
            .catch(error => {
                resolve({workshop:null,errormessage:error.message});
            });

    })
};

export default readWorkshopFromServer;
